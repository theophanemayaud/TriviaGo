package com.laubasthe.triviago.trivia;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TriviaClient {

    public static final String BASE_URL = "https://opentdb.com/";

    public static TriviaService getTriviaClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TriviaService.class);
    }
}
