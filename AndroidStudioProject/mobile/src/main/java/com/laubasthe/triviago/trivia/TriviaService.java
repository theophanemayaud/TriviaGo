package com.laubasthe.triviago.trivia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaService {
    @GET("/api.php?amount=1&type=multiple")
    Call<TriviaModel> getQCMQuestion(@Query("category") String category, @Query("difficulty") String difficulty);

    @GET("/api.php?amount=1&type=boolean")
    Call<TriviaModel> getTFQuestion(@Query("category") String category, @Query("difficulty") String difficulty);
}
