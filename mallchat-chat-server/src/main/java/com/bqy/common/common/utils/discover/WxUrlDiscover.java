package com.bqy.common.common.utils.discover;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Document;

public class WxUrlDiscover extends AbstractUrlDiscover {
    @Override
    public @Nullable String getTitle(Document document) {
        return document.getElementsByAttributeValue("property", "og:title").attr("content");
    }

    @Override
    public @Nullable String getDescription(Document document) {
        return document.getElementsByAttributeValue("property", "og:description").attr("content");
    }

    @Override
    public @Nullable String getImg(String url, Document document) {
        String href = document.getElementsByAttributeValue("property", "og:image").attr("content");
        return isConnect(href) ? href : null;
    }
}
