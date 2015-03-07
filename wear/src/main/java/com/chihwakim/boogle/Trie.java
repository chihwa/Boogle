package com.chihwakim.boogle;

public class Trie {
    TrieNode root = new TrieNode();

    static class TrieNode {
        TrieNode[] children = new TrieNode[128];
        boolean leaf;
    }

    public void insertString(String s) {
        TrieNode v = root;
        for (char ch : s.toCharArray()) {
            TrieNode next = v.children[ch];
            if (next == null)
                v.children[ch] = next = new TrieNode();
            v = next;
        }
        v.leaf = true;
    }

    public boolean exists(String s) {
        TrieNode v = root;
        for (char ch : s.toCharArray()) {
            TrieNode next = v.children[ch];
            if (next == null) {
                return false;
            }
            v = next;
        }
        return v.leaf;
    }

    public boolean hasPath(String s) {
        TrieNode v = root;
        for (char ch : s.toCharArray()) {
            TrieNode next = v.children[ch];
            if (next == null) {
                return false;
            }
            v = next;
        }
        return v != null;
    }


    public void printSorted(TrieNode node, String s) {
        for (char ch = 0; ch < node.children.length; ch++) {
            TrieNode child = node.children[ch];
            if (child != null)
                printSorted(child, s + ch);
        }
        if (node.leaf) {
            System.out.println(s);
        }
    }
}