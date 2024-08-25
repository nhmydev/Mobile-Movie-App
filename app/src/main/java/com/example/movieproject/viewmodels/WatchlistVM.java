package com.example.movieproject.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.movieproject.database.TVShowDatabase;
import com.example.movieproject.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchlistVM extends AndroidViewModel {

    private TVShowDatabase tvShowDatabase;
    public WatchlistVM(@NonNull Application application){
        super(application);
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }
    public Flowable<List<TVShow>> loadWatchlist(){
        return tvShowDatabase.tvShowDao().getWatchlist();
    }
    public Completable removeTVShow(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }
}
