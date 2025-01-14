package com.bqy.common.common.utils.discover;

import com.bqy.common.common.utils.discover.domain.UrlInfo;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Document;

import java.util.Map;

public interface UrlDiscover {
    @Nullable
    Map<String, UrlInfo> getUrlContentMap(String content);

    @Nullable
    UrlInfo getContent(String url);

    @Nullable
    String getTitle(Document document);

    @Nullable
    String getDescription(Document document);

    @Nullable
    String getImg(String url,Document document);
}
