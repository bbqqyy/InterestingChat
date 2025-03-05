package com.bqy.common.common.algorithm.sensitiveWord;

import java.util.List;

public class SentinelWordBs {
    private SentinelWordBs(){

    }
    private SensitiveWordFilter sensitiveWordFilter = DFAFilter.getInstance();
    private IWordFactory wordDeny;
    public static SentinelWordBs getInstance(){
        return new SentinelWordBs();
    }
    public SentinelWordBs init(){
        List<String> wordList = wordDeny.getWordList();
        loadWord(wordList);
        return this;
    }
    public boolean hasSensitiveWord(String text){
        return sensitiveWordFilter.hasSensitiveWord(text);
    }
    public String filter(String text){
        return sensitiveWordFilter.filter(text);
    }
    public void loadWord(List<String> wordList){
        sensitiveWordFilter.loadWord(wordList);
    }

}
