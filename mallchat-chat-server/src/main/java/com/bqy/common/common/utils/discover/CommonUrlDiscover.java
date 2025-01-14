package com.bqy.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Document;

public class CommonUrlDiscover extends AbstractUrlDiscover{

    @Override
    public @Nullable String getTitle(Document document) {
        return document.title();
    }

    @Override
    public @Nullable String getDescription(Document document) {
        String description = document.head().select("meta[description]").attr("content");
        String keywords = document.head().select("meta[name=keywords").attr("content");
        String content = StrUtil.isNotBlank(description)?description:keywords;
        return StrUtil.isNotBlank(content)?content.substring(0,content.indexOf("。")):content;
    }

    @Override
    public @Nullable String getImg(String url, Document document) {
        String img = document.select("link[type=image/x-icon]").attr("href");
        String href = StrUtil.isEmpty(img)?document.select("link[rel$=icon]").attr("href"):img;
        if(StrUtil.containsAny(url,"favicon")){
            return url;
        }
        if(isConnect(!StrUtil.startWith(href,"http")?"http:"+href:href)){
            return href;
        }
        return StrUtil.format("{}/{}",url,StrUtil.removePrefix(href,"/"));
    }
}
