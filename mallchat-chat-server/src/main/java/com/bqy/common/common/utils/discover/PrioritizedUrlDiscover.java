package com.bqy.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class PrioritizedUrlDiscover extends AbstractUrlDiscover{
    private final List<UrlDiscover> urlDiscovers = new ArrayList<>(2);
    public PrioritizedUrlDiscover(){
        urlDiscovers.add(new CommonUrlDiscover());
        urlDiscovers.add(new WxUrlDiscover());
    }
    @Override
    public @Nullable String getTitle(Document document) {
        for (UrlDiscover urlDiscover:urlDiscovers){
            String urlTitle = urlDiscover.getTitle(document);
            if(StrUtil.isNotBlank(urlTitle)){
                return urlTitle;
            }
        }
        return null;
    }

    @Override
    public @Nullable String getDescription(Document document) {
        for(UrlDiscover urlDiscover:urlDiscovers){
            String urlDescription = urlDiscover.getDescription(document);
            if(StrUtil.isNotBlank(urlDescription)){
                return urlDescription;
            }
        }
        return null;
    }

    @Override
    public @Nullable String getImg(String url, Document document) {
        for(UrlDiscover urlDiscover:urlDiscovers){
            String urlImg = urlDiscover.getImg(url,document);
            if(StrUtil.isNotBlank(urlImg)){
                return urlImg;
            }
        }
        return null;
    }
}
