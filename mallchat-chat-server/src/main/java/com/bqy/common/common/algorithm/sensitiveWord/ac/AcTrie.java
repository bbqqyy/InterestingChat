package com.bqy.common.common.algorithm.sensitiveWord.ac;

import org.apache.commons.compress.utils.Lists;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;
import java.util.stream.Collectors;

@NotThreadSafe
public class AcTrie {

    private AcTrieNode root;

    public AcTrie(List<String> words) {
        words = words.stream().distinct().collect(Collectors.toList());
        root = new AcTrieNode();
        for (String word : words) {
            addWord(word);
        }
        initFailover();
    }

    public void initFailover() {
        Queue<AcTrieNode> queue = new LinkedList<>();
        Map<Character, AcTrieNode> child = root.getChildren();
        for (AcTrieNode node : child.values()) {
            node.setFailover(root);
            queue.offer(node);
        }
        while (!queue.isEmpty()) {
            AcTrieNode parentNode = queue.poll();
            for (Map.Entry<Character, AcTrieNode> entry : parentNode.getChildren().entrySet()) {
                AcTrieNode childNode = entry.getValue();
                AcTrieNode failOver = parentNode.getFailover();
                while (failOver!=null&&(!failOver.hasChild(entry.getKey()))){
                    failOver = failOver.getFailover();
                }
                if(failOver==null){
                    childNode.setFailover(root);
                }else {
                    childNode.setFailover(failOver.childOf(entry.getKey()));
                }
                queue.offer(childNode);
            }
        }
    }

    public void addWord(String word) {
        AcTrieNode walkNode = root;
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            walkNode.addChildIfAbsent(chars[i]);
            walkNode = walkNode.childOf(chars[i]);
            walkNode.setDepth(i + 1);
        }
        walkNode.setLeaf(true);
    }
    public List<MatchResult> matches(String text){
        List<MatchResult> result = Lists.newArrayList();
        AcTrieNode walkNode = root;
        for(int i = 0;i<text.length();i++){
            char c = text.charAt(i);
            while(!walkNode.hasChild(c)&&walkNode.getFailover()!=null){
                walkNode = walkNode.getFailover();
            }
            if(walkNode.hasChild(c)){
                walkNode = walkNode.childOf(c);
                if(walkNode.isLeaf()){
                    result.add(new MatchResult(i-walkNode.getDepth()+1,i+1));
                }
            }
        }
        return result;
    }

}
