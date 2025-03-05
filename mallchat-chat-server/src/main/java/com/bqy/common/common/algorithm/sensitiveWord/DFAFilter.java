package com.bqy.common.common.algorithm.sensitiveWord;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;

public final class DFAFilter implements SensitiveWordFilter {
    private DFAFilter() {

    }

    private static Word root = new Word(' ');
    private final static char replace = '*';
    private final static String skipChars = " !*-+_=,，.@;:；：。、？?（）()【】[]《》<>“”\"‘’"; // 遇到这些字符就会跳过
    private final static Set<Character> skipSet = new HashSet<>();

    static {
        for (char c : skipChars.toCharArray()) {
            skipSet.add(c);
        }
    }

    public static DFAFilter getInstance() {
        return new DFAFilter();
    }

    @Override
    public boolean hasSensitiveWord(String text) {
        if(StrUtil.isBlank(text)) return false;
        return !ObjectUtil.equals(filter(text),text);
    }

    @Override
    public String filter(String text) {
        StringBuilder result = new StringBuilder(text);
        int index = 0;
        while (index < result.length()) {
            char c = result.charAt(index);
            if(skipSet.contains(c)){
                index++;
                continue;
            }
            Word word = root;
            int start = index;
            boolean found = false;
            for(int i = index;i<result.length();i++){
                c = result.charAt(i);
                if(skipSet.contains(c)){
                    continue;
                }
                if(c>='A'&&c<='Z'){
                    c+=32;
                }
                word = word.next.get(c);
                if(word==null){
                    break;
                }
                if(word.end){
                    found = true;
                    for(int j = start;j<=i;j++){
                        result.setCharAt(j,replace);
                    }
                    index = i;
                }
            }
            if(!found){
                index++;
            }
        }
        return result.toString();
    }

    @Override
    public void loadWord(List<String> words) {
        if(CollectionUtil.isNotEmpty(words)){
            Word newRoot = new Word(' ');
            words.forEach(word->loadWord(word,newRoot));
            root = newRoot;
        }
    }
    public void loadWord(String word,Word root){
        if(StrUtil.isBlank(word)){
            return;
        }
        Word current = root;
        for(int i = 0;i<word.length();i++){
            char c = word.charAt(i);
            if(c>='A'&&c<='Z'){
                c+=32;
            }
            if(skipSet.contains(c)){
                continue;
            }
            Word next = current.next.get(c);
            if(next==null){
                next = new Word(c);
                current.next.put(c,next);
            }
            current = next;
        }
        current.end = true;
    }

    private static class Word {
        private final char c;
        private boolean end;
        private Map<Character, Word> next;

        public Word(char c) {
            this.c = c;
            next = new HashMap<>();
        }
    }
}
