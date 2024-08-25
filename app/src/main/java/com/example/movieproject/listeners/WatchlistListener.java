package com.example.movieproject.listeners;

import com.example.movieproject.models.TVShow;

public interface WatchlistListener {
    void onTVShowClicked(TVShow tvShow);
    void removeTVShowFromWL(TVShow tvShow,int position);
}
