package com.bqy.common.common.algorithm.sensitiveWord;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.algorithm.sensitiveWord.acpro.AcProTrie;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AcProFilter implements SensitiveWordFilter {
    private AcProTrie acProTrie;
    @Override
    public boolean hasSensitiveWord(String text) {
        if(StringUtils.isBlank(text)) return false;
        return !ObjectUtil.equals(text,filter(text));
    }

    @Override
    public String filter(String text) {
        return acProTrie.match(text);
    }

    @Override
    public void loadWord(List<String> words) {
        if(CollectionUtil.isEmpty(words)){
            return;
        }
        acProTrie = new AcProTrie();
        acProTrie.createAcTrie(words);
    }
}
