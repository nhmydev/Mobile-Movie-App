package com.example.movieproject.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieproject.database.TVShowDatabase;
import com.example.movieproject.models.TVShow;
import com.example.movieproject.repositories.TVShowDetailsRepository;
import com.example.movieproject.responses.TVShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsVM extends AndroidViewModel {
    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowDatabase tvShowDatabase;
    public TVShowDetailsVM(@NonNull Application application){
        super(application);
        tvShowDetailsRepository= new TVShowDetailsRepository();
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }
    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId){
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }
    public Completable addToWatchlist(TVShow tvShow){
        return tvShowDatabase.tvShowDao().addToWatchlist(tvShow);
    }

    public Flowable<TVShow> getTVShowFromWL(String tvShowId){
        return tvShowDatabase.tvShowDao().getTVShowFromWL(tvShowId);
    }
    public Completable removeTVShowFromWL(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }
}
