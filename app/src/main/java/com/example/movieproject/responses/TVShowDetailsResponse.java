package com.example.movieproject.responses;

import com.example.movieproject.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowDetailsResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public  TVShowDetails getTvShowDetails(){
        return tvShowDetails;
    }
}
