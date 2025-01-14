package com.bqy.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.handler.GlobalUncaughtExceptionHandler;
import com.bqy.common.common.utils.JsonUtils;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.IpDetail;
import com.bqy.common.user.domain.entity.IpInfo;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.service.IpService;
import com.bqy.common.user.service.cache.UserCache;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService, DisposableBean {
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false,
                    GlobalUncaughtExceptionHandler.getInstance()));
    @Resource
    private UserDao userDao;

    @Resource
    private UserCache userCache;

    @Override
    public void refreshIpDetailAsync(Long id) {
        executor.execute(() -> {
            User user = userDao.getById(id);
            IpInfo ipInfo = user.getIpInfo();
            if (ObjectUtil.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.isNeedRefresh();
            if (StringUtils.isBlank(ip)) {
                return;
            }
            IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes(ip);
            if (ObjectUtil.isNotNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(id);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
                userCache.userInfoChange(id);
            }else {
                log.error("get ip detail fail ip:{},uid:{}", ip, id);
            }
        });
    }

    private static IpDetail tryGetIpDetailOrNullTreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = tryGetIpDetailOrNull(ip);
            if (ObjectUtil.isNotNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("tryGetIpDetailOrNullTreeTimes InterruptedException", e);
            }
        }
        return null;
    }

    private static IpDetail tryGetIpDetailOrNull(String ip) {
        try {
            String url = "https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc";
            String data = HttpUtil.get(url);
            ApiResult<IpDetail> result = JsonUtils.toObj(data, new TypeReference<ApiResult<IpDetail>>() {
            });
            IpDetail resultData = result.getData();
            return resultData;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            int count = i;
            executor.execute(() -> {
                IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes("117.85.133.4");
                if (ObjectUtil.isNotNull(ipDetail)) {
                    Date end = new Date();
                    System.out.println(String.format("成功%d次,耗时%dms", count, (end.getTime() - begin.getTime())));
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            if (log.isErrorEnabled()) {
                log.error("Time out while waiting for executor[{}] to Terminate", executor);
            }
        }
    }
}
