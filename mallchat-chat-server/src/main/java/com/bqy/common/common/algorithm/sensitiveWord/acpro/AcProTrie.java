package com.bqy.common.common.algorithm.sensitiveWord.acpro;

import java.util.*;

public class AcProTrie {
    private final static char MASK = '*';
    private Word root;

    static class Word {
        boolean end = false;
        Word failOver = null;
        int depth = 0;
        Map<Character, Word> next = new HashMap<>();

        public boolean hasChild(char c) {
            return next.containsKey(c);
        }
    }

    public void createAcTrie(List<String> wordList) {
        Word currentNode = new Word();
        root = currentNode;
        for (String key : wordList) {
            currentNode = root;
            for (int i = 0; i < key.length(); i++) {
                if (currentNode.next != null && currentNode.next.containsKey(key.charAt(i))) {
                    currentNode = currentNode.next.get(key.charAt(i));
                    if (i == key.length() - 1) {
                        currentNode.end = true;
                    }
                } else {
                    Word map = new Word();
                    if (i == key.length() - 1) {
                        currentNode.end = true;
                    }
                    currentNode.next.put(key.charAt(i), map);
                    currentNode = map;
                }
                currentNode.depth = i + 1;
            }
        }
        initFailOver();
    }

    private void initFailOver() {
        Queue<Word> queue = new LinkedList<>();
        Map<Character, Word> children = root.next;
        for (Word node : children.values()) {
            node.failOver = root;
            queue.offer(node);
        }
        while (!queue.isEmpty()) {
            Word parentNode = queue.poll();
            for (Map.Entry<Character,Word> entry: parentNode.next.entrySet()){
                Word childrenNode = entry.getValue();
                Word failOver = parentNode.failOver;
                while (failOver!=null&&!failOver.next.containsKey(entry.getKey())){
                    failOver.failOver = failOver;
                }
                if(failOver==null){
                    childrenNode.failOver = root;
                }else {
                    childrenNode.failOver = failOver.next.get(entry.getKey());
                }
                queue.offer(childrenNode);
            }
        }
    }
    public String match(String matchWord){
        Word walkNode = root;
        char[] wordArray = matchWord.toCharArray();
        for(int i = 0;i<wordArray.length;i++){
            while(!walkNode.hasChild(wordArray[i]) && walkNode.failOver!=null){
                walkNode = walkNode.failOver;
            }
            if(walkNode.hasChild(wordArray[i])){
                walkNode = walkNode.next.get(wordArray[i]);
                if(walkNode.end){
                    Word sentinelA = walkNode;
                    Word sentinelB = walkNode;
                    int k = i + 1;
                    boolean flag = false;
                    while(k<wordArray.length&&sentinelA.hasChild(wordArray[k])){
                        sentinelA = sentinelA.next.get(wordArray[k]);
                        k++;
                        if(sentinelA.end){
                            sentinelB = sentinelA;
                            flag = true;
                        }
                    }
                    int len = flag?sentinelB.depth:walkNode.depth;
                    while(len>0){
                        len--;
                        int index = flag?i-walkNode.depth+1+len:i-len;
                        wordArray[index] = MASK;
                    }
                    i+=flag?sentinelB.depth:0;
                    walkNode = flag?sentinelB.failOver:walkNode.failOver;
                }
            }
        }
        return new String(wordArray);
    }
}
