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
        Categories.add(" ");
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

    static final ArrayList<String> Bg_colors = new ArrayList<>();
    static {
        Bg_colors.add("#128314"); // General knowledge
        Bg_colors.add("#C51A56"); // Entertainment: Books
        Bg_colors.add("#BC3866"); // Entertainment: Film
        Bg_colors.add("#F30D5D"); // Entertainment: Music
        Bg_colors.add("#980538"); // Entertainment: Musicals & Theatres
        Bg_colors.add("#C1547A"); // Entertainment: Television
        Bg_colors.add("#FD7AA7"); // Entertainment: Video Games
        Bg_colors.add("#85002E"); // Entertainment: Board Games
        Bg_colors.add("#508C64"); // Science & Nature
        Bg_colors.add("#1C783B"); // Science: Computers
        Bg_colors.add("#044A1C"); // Science: Mathematics
        Bg_colors.add("#DFA44B"); // Mythology
        Bg_colors.add("#322AA3"); // Sports
        Bg_colors.add("#294E08"); // Geography
        Bg_colors.add("#B5A881"); // History
        Bg_colors.add("#8B81B5"); // Politics
        Bg_colors.add("#13611A"); // Art
        Bg_colors.add("#D552F3"); // Celebrities
        Bg_colors.add("#8F7A84"); // Animals
        Bg_colors.add("#102E33"); // Vehicles
        Bg_colors.add("#A643B6"); // Entertainment: Comics
        Bg_colors.add("#32C35C"); // Science: Gadgets
        Bg_colors.add("#DF7B9E"); // Entertainment: Japanese Anime & Manga
        Bg_colors.add("#DCA9BB"); // Entertainment: Cartoon & Animations
    }

    static final ArrayList<Integer> Cat_icons = new ArrayList<>();
    static {
        Cat_icons.add(R.mipmap.general_knoledge); // General knowledge
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Books
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Film
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Music
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Musicals & Theatres
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Television
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Video Games
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Board Games
        Cat_icons.add(R.mipmap.science); // Science & Nature
        Cat_icons.add(R.mipmap.science); // Science: Computers
        Cat_icons.add(R.mipmap.science); // Science: Mathematics
        Cat_icons.add(R.mipmap.myths); // Mythology
        Cat_icons.add(R.mipmap.sports); // Sports
        Cat_icons.add(R.mipmap.geographie); // Geography
        Cat_icons.add(R.mipmap.history); // History
        Cat_icons.add(R.mipmap.politics); // Politics
        Cat_icons.add(R.mipmap.art); // Art
        Cat_icons.add(R.mipmap.celeb); // Celebrities
        Cat_icons.add(R.mipmap.animals); // Animals
        Cat_icons.add(R.mipmap.vehicule); // Vehicles
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Comics
        Cat_icons.add(R.mipmap.science); // Science: Gadgets
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Japanese Anime & Manga
        Cat_icons.add(R.mipmap.entertainment); // Entertainment: Cartoon & Animations
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
    public static final Integer TRIVIA_SPINNER_API_OFFSET = 8;
    public static final Integer TRIVIA_API_ARRAY_OFFSET = 9;
    public static final Integer MAX_DIFFICULTY = 3;

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
}