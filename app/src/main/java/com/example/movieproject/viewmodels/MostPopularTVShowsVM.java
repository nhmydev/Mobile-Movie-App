package com.example.movieproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieproject.repositories.MostPopularTVShowsRepository;
import com.example.movieproject.responses.TVShowResponse;

public class MostPopularTVShowsVM extends ViewModel {
    private MostPopularTVShowsRepository mostPopularTVShowsRepository;
    public  MostPopularTVShowsVM(){
        mostPopularTVShowsRepository = new MostPopularTVShowsRepository();
    }
    public LiveData<TVShowResponse> getMostPopularTVShows(int page){
        return mostPopularTVShowsRepository.getMostPopularTVShows(page);
    }
}
