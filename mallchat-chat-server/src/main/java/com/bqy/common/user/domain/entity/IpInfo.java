package com.bqy.common.user.domain.entity;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Optional;

@Data
public class IpInfo implements Serializable {

    private String createIp;
    private IpDetail createIpDetail;
    private String updateIp;
    private IpDetail updateIpDetail;

    public void refreshIp(String ip) {
        if(StringUtils.isBlank(ip)){
            return;
        }
        if(StringUtils.isBlank(createIp)){
            createIp = ip;
        }
        updateIp = ip;
    }

    public String isNeedRefresh() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> ObjectUtil.equals(updateIp, ip))
                .isPresent();
        return notNeedRefresh?null:updateIp;


    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if(ObjectUtil.equals(createIp,ipDetail.getIp())){
            createIpDetail = ipDetail;
        }
        if(ObjectUtil.equals(updateIp,ipDetail.getIp())){
            updateIpDetail = ipDetail;
        }
    }
}
