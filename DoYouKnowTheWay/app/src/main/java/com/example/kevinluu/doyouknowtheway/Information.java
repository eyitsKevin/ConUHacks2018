package com.example.kevinluu.doyouknowtheway;

import java.text.DecimalFormat;

/**
 * Created by kevinluu on 2018-01-28.
 */

public class Information {
    private String word;
    private String score;

    public Information(String w, String s) {
        this.word = w;
        this.score = s;
    }

    public int getScore() {

        return (int) (Double.parseDouble(score.replaceAll("\"", ""))*100);
    }

    public String toString(){
        return "This has a " + getScore() + " percent of being " + word;
    }
}