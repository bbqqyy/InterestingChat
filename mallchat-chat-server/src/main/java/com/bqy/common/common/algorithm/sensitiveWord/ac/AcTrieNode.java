package com.bqy.common.common.algorithm.sensitiveWord.ac;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class AcTrieNode {
    private Map<Character,AcTrieNode> children = Maps.newHashMap();
    private AcTrieNode failover = null;
    private int depth;
    private boolean isLeaf = false;
    public void addChildIfAbsent(char c){
        children.computeIfAbsent(c,(key)-> new AcTrieNode());
    }
    public AcTrieNode childOf(char c){
        return children.get(c);
    }
    public boolean hasChild(char c){
        return children.containsKey(c);
    }
    @Override
    public String toString() {
        return "ACTrieNode{" +
                "failover=" + failover +
                ", depth=" + depth +
                ", isLeaf=" + isLeaf +
                '}';
    }
}
