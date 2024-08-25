package com.example.movieproject.responses;

import com.example.movieproject.models.TVShow;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowResponse {

    @SerializedName("page")
    private int page;
    @SerializedName("pages")
    private int totalPage;

    @SerializedName("tv_shows")
    private List<TVShow> tvShows;

    public int getPage() {
        return page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public List<TVShow> getTvShows() {
        return tvShows;
    }
}
