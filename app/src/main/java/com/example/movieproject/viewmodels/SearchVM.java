package com.example.movieproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieproject.repositories.SearchTVShowRepo;
import com.example.movieproject.responses.TVShowResponse;

public class SearchVM extends ViewModel {
    private SearchTVShowRepo searchTVShowRepo;
    public SearchVM(){
        searchTVShowRepo =new SearchTVShowRepo();
    }
    public LiveData<TVShowResponse> searchTVShow(String query,int page){
        return searchTVShowRepo.searchTVShow(query,page);
    }
}
