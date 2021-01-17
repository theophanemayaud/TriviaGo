package com.epfl.triviago.trivia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TriviaModel {

    @SerializedName("response_code")
    @Expose
    private Integer responseCode;
    @SerializedName("results")
    @Expose
    private List<TriviaResult> results = null;

    public Integer getResponseCode() {
        return responseCode;
    }

    public List<TriviaResult> getResults() {
        return results;
    }

}
