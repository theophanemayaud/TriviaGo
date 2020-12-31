package com.epfl.triviago;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TriviaQuestion implements Serializable {

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

    static final ArrayList<String> Categories = new ArrayList<>();
    static {
        Categories.add("General Knowledge");
        Categories.add("Entertainment: Books");
        Categories.add("Entertainment: Film");
        Categories.add("Entertainment: Music");
        Categories.add("Entertainment: Musicals & Theatres");
        Categories.add("Entertainment: Television");
        Categories.add("Entertainment: Video Games");
        Categories.add("Entertainment: Board Games");
        Categories.add("Science & Nature");
        Categories.add("Science: Computers");
        Categories.add("Science: Mathematics");
        Categories.add("Mythology");
        Categories.add("Sports");
        Categories.add("Geography");
        Categories.add("History");
        Categories.add("Politics");
        Categories.add("Art");
        Categories.add("Celebrities");
        Categories.add("Animals");
        Categories.add("Vehicles");
        Categories.add("Entertainment: Comics");
        Categories.add("Science: Gadgets");
        Categories.add("Entertainment: Japanese Anime & Manga");
        Categories.add("Entertainment: Cartoon & Animations");
    }

    static final ArrayList<String> DIFFICULTY = new ArrayList<>();
    static {
        DIFFICULTY.add("easy");
        DIFFICULTY.add("medium");
        DIFFICULTY.add("hard");
    }

    private final Integer MAX_INDEX_QCM = 4;
    private final Integer MAX_INDEX_BOOL = 2;

    public static final Integer MAX_CATEGORIES = 23;

    public String mQuestion;
    public String mDifficulty;
    public String mCategory;
    public ArrayList<String> mResponses = new ArrayList();
    public Integer mCorrectIndex;

    public TriviaQuestion(String question, String difficulty, String category, List<String> incorrectResponses, String correctResponse, String type) {
        this.mQuestion = question;
        this.mDifficulty = difficulty;
        this.mCategory = category;
        if (!type.equals("boolean")) {
            this.mCorrectIndex = new Random().nextInt(MAX_INDEX_QCM);

        } else {
            this.mCorrectIndex = new Random().nextInt(MAX_INDEX_BOOL);
        }
        this.mResponses.addAll(incorrectResponses);
        this.mResponses.add(mCorrectIndex, correctResponse);
    }

    public static String getCategoryTextFromInt(int category) {
        return Categories.get(category);
    }
}