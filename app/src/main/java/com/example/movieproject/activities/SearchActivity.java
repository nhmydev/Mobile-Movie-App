package com.example.movieproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.movieproject.R;
import com.example.movieproject.adapters.TVShowsAdapter;
import com.example.movieproject.databinding.ActivityMainBinding;
import com.example.movieproject.databinding.ActivitySearchBinding;
import com.example.movieproject.listeners.TVShowListener;
import com.example.movieproject.models.TVShow;
import com.example.movieproject.viewmodels.SearchVM;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowListener {
    private ActivitySearchBinding activitySearchBinding;
    private SearchVM viewmodel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private  int currentPage = 1;
    private  int totalPages = 1;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = DataBindingUtil.setContentView(this,R.layout.activity_search);
        init();
    }
    private void init(){
        activitySearchBinding.imageBack.setOnClickListener(v -> onBackPressed());
        activitySearchBinding.tvShowsRecyclerView.setHasFixedSize(true);
        viewmodel = new ViewModelProvider(this).get(SearchVM.class);
        tvShowsAdapter =  new TVShowsAdapter(tvShows,this);
        activitySearchBinding.tvShowsRecyclerView.setAdapter(tvShowsAdapter);
        activitySearchBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(timer !=null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().trim().isEmpty()){
                    timer =new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalPages = 1;
                                searchTVShow(s.toString());
                            });
                        }
                    },800);
                }else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
        activitySearchBinding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activitySearchBinding.tvShowsRecyclerView.canScrollVertically(1)){
                    if (!activitySearchBinding.inputSearch.getText().toString().isEmpty()){
                        if (currentPage<totalPages)
                        {
                            currentPage+=1;
                            searchTVShow(activitySearchBinding.inputSearch.getText().toString());
                        }
                    }
                }
            }
        });
        activitySearchBinding.inputSearch.requestFocus();
    }
    private void searchTVShow(String query){
        toggleLoading();
        viewmodel.searchTVShow(query,currentPage).observe(this,tvShowResponse -> {
            toggleLoading();
            if(tvShowResponse!=null){
                totalPages = tvShowResponse.getTotalPage();
                if(tvShowResponse.getTvShows()!=null){
                    int oldCount = tvShows.size();
                    tvShows.addAll(tvShowResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount,tvShows.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (activitySearchBinding.getIsLoading() != null && activitySearchBinding.getIsLoading()) {
                activitySearchBinding.setIsLoading(false);
            } else {
                activitySearchBinding.setIsLoading(true);
            }
        } else {
            if (activitySearchBinding.getIsLoadingMore() != null && activitySearchBinding.getIsLoadingMore()) {
                    activitySearchBinding.setIsLoadingMore(false);
            } else {
                activitySearchBinding.setIsLoadingMore(true);
            }
        }
    }
    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}