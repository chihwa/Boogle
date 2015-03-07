package com.chihwakim.boogle;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Dictionary {
    public static final int MAXCHAR = 6;
    private final int resid;
    public Trie trie = new Trie();

    public Context ctx;

    public Dictionary(Context ctx, int resid) {
        this.ctx = ctx;
        this.resid = resid;
    }

    public void buildDic() throws IOException {
        InputStream inputStream = ctx.getResources().openRawResource(this.resid);
        InputStreamReader ir = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(ir);

        String word;
        while ((word = br.readLine()) != null) {
            word = word.trim();
            if (word.length() <= MAXCHAR) trie.insertString(word.toUpperCase());
        }
    }
}