package com.bqy.common.common.utils.discover;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.bqy.common.common.utils.FutureUtils;
import com.bqy.common.common.utils.discover.domain.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.util.Pair;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractUrlDiscover implements UrlDiscover {
    //链接识别的正则
    private static final Pattern PATTERN = Pattern.compile("((http|https)://)?(www.)?([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

    @Override
    public @Nullable Map<String, UrlInfo> getUrlContentMap(String content) {
        if (StrUtil.isBlank(content)) {
            return new HashMap<>();
        }
        List<String> matchList = ReUtil.findAll(PATTERN, content, 0);
        List<CompletableFuture<Pair<String, UrlInfo>>> futures = matchList.stream()
                .map(match -> CompletableFuture.supplyAsync(() -> {
                    UrlInfo urlInfo = getContent(match);
                    return Objects.isNull(urlInfo) ? null : Pair.of(match, urlInfo);
                })).collect(Collectors.toList());
        CompletableFuture<List<Pair<String, UrlInfo>>> future = FutureUtils.sequenceNonNull(futures);
        return future.join().stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (a, b) -> a));
    }

    @Override
    public @Nullable UrlInfo getContent(String url) {
        Document document = getUrlDocument(assemble(url));
        if (ObjectUtil.isNull(document)) {
            return null;
        }
        return UrlInfo.builder()
                .title(getTitle(document))
                .description(getDescription(document))
                .image(getImg(assemble(url), document))
                .build();

    }

    protected Document getUrlDocument(String matchUrl) {
        try {
            Connection connection = Jsoup.connect(matchUrl);
            connection.timeout(2000);
            return connection.get();
        } catch (Exception e) {
            log.error("find error:url:{}", matchUrl, e);
        }
        return null;
    }

    private String assemble(String url) {
        if (!StrUtil.startWith(url, "http")) {
            return "http://" + url;
        }
        return url;
    }

    public static boolean isConnect(String href) {
        //请求地址
        URL url;
        //请求状态码
        int state;
        //下载链接类型
        String fileType;
        try {
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            //如果成功200，缓存304，移动302都算有效链接,并且不是下载链接
            if ((state == 200 || state == 304 || state == 302) && fileType == null) {
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
