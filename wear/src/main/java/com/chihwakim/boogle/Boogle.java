package com.chihwakim.boogle;

import java.util.ArrayList;
import java.util.List;

public class Boogle {

    public int M;
    public int N;
    public Dictionary dic;

    public Boogle(Dictionary dic) {
        this.dic = dic;
    }

    boolean isWord(String str) {
        return dic.trie.exists(str);
    }

    void findWordsUtil(char boggle[][], boolean visited[][], int i,
                       int j, StringBuilder str, List<String> result) {
        visited[i][j] = true;
        str = str.append(boggle[i][j]);

        if (isWord(str.toString())) result.add(str.toString());

        for (int row = i - 1; row <= i + 1 && row < M; row++)
            for (int col = j - 1; col <= j + 1 && col < N; col++)
                if (row >= 0 && col >= 0 && !visited[row][col] && dic.trie.hasPath(str.toString()))
                    findWordsUtil(boggle, visited, row, col, str, result);

        str.deleteCharAt(str.length() - 1);
        visited[i][j] = false;
    }

    public List<String> findWords(char[][] boggle) {
        this.M = boggle.length;
        this.N = boggle[0].length;
        boolean visited[][] = new boolean[M][N];

        StringBuilder str = new StringBuilder();

        List<String> result = new ArrayList<String>();

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                findWordsUtil(boggle, visited, i, j, str, result);

        return result;
    }
}
