package com.example.kevinluu.doyouknowtheway;

import java.text.DecimalFormat;

/**
 * Created by kevinluu on 2018-01-28.
 */

public class Information {
    private String word;
    private String score;
    private int score2;

    public Information() {
        this.word = "";
        this.score = "";
    }

    public Information(String w, String s) {
        this.word = w;
        this.score = s;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWord() {
        return word;
    }

    public int getScore() {
        return (int) (Double.parseDouble(score.replaceAll("\"", ""))*100);
    }


    public String toString(){
        return "This has a " + getScore() + " percent of being " + word;
    }
}