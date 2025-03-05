package com.bqy.common.common.algorithm.sensitiveWord;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.algorithm.sensitiveWord.ac.AcTrie;
import com.bqy.common.common.algorithm.sensitiveWord.ac.MatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AcFilter implements SensitiveWordFilter {
    private final static char replace = '*';
    private static AcTrie ac_trie = null;

    @Override
    public boolean hasSensitiveWord(String text) {
        if(StringUtils.isBlank(text)) return false;
        return !ObjectUtil.equals(filter(text),text);
    }

    /**
     * 敏感词替换
     *
     * @param text 文本
     * @return
     */
    @Override
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        List<MatchResult> matchResults = ac_trie.matches(text);
        StringBuffer result = new StringBuffer(text);
        int endIndex = 0;
        for (MatchResult matchResult : matchResults) {
            endIndex = Math.max(matchResult.getEndIndex(),endIndex);
            replaceBetween(result,matchResult.getStartIndex(),endIndex);
        }
        return result.toString();

    }

    private static void replaceBetween(StringBuffer result, int startIndex, int endIndex) {
        for(int i = startIndex;i<endIndex;i++){
            result.setCharAt(i,replace);
        }
    }

    @Override
    public void loadWord(List<String> words) {
        if (words == null) return;
        ac_trie = new AcTrie(words);
    }
}
