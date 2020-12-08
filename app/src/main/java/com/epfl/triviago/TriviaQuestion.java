package com.epfl.triviago;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TriviaQuestion implements Serializable {
//
//    static final HashMap<String, Integer> CATEGORIES = new HashMap();
//    static {
//        CATEGORIES.put("General Knowledge", 9);
//        CATEGORIES.put("Entertainment: Books", 10);
//        CATEGORIES.put("Entertainment: Film", 11);
//        CATEGORIES.put("Entertainment: Music", 12);
//        CATEGORIES.put("Entertainment: Musicals & Theatres", 13);
//        CATEGORIES.put("Entertainment: Television", 14);
//        CATEGORIES.put("Entertainment: Video Games", 15);
//        CATEGORIES.put("Entertainment: Board Games", 16);
//        CATEGORIES.put("Science & Nature", 17);
//        CATEGORIES.put("Science: Computers", 18);
//        CATEGORIES.put("Science: Mathematics", 19);
//        CATEGORIES.put("Mythology", 20);
//        CATEGORIES.put("Sports", 21);
//        CATEGORIES.put("Geography", 22);
//        CATEGORIES.put("History", 23);
//        CATEGORIES.put("Politics", 24);
//        CATEGORIES.put("Art", 25);
//        CATEGORIES.put("Celebrities", 26);
//        CATEGORIES.put("Animals", 27);
//        CATEGORIES.put("Vehicles", 28);
//        CATEGORIES.put("Entertainment: Comics", 29);
//        CATEGORIES.put("Science: Gadgets", 30);
//        CATEGORIES.put("Entertainment: Japanese Anime & Manga", 31);
//        CATEGORIES.put("Entertainment: Cartoon & Animations", 32);
//    }
//
//    static final ArrayList<String> DIFFICULTY = new ArrayList<>();
//    static {
//        DIFFICULTY.add("easy");
//        DIFFICULTY.add("medium");
//        DIFFICULTY.add("hard");
//    }

    private final Integer MAX_INDEX = 4;

    public String mQuestion;
    public String mDifficulty;
    public String mCategory;
    public ArrayList<String> mResponses = new ArrayList<String>();
    public Integer mcorrectIndex;

    public TriviaQuestion(String question, String difficulty, String category, List<String> incorrectResponses, String correctResponse) {
        this.mQuestion = question;
        this.mDifficulty = difficulty;
        this.mCategory = category;
        this.mcorrectIndex = new Random().nextInt(MAX_INDEX);
        this.mResponses.addAll(incorrectResponses);
        this.mResponses.add(mcorrectIndex, correctResponse);
    }
}