package com.example.movieproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.movieproject.R;
import com.example.movieproject.adapters.WatchlistAdapter;
import com.example.movieproject.databinding.ActivityWatchlistBinding;
import com.example.movieproject.listeners.WatchlistListener;
import com.example.movieproject.models.TVShow;
import com.example.movieproject.uitilities.TempDataHolder;
import com.example.movieproject.viewmodels.WatchlistVM;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity implements WatchlistListener {
    private ActivityWatchlistBinding activityWatchlistBinding;
    private WatchlistVM watchlistVM;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = DataBindingUtil.setContentView(this, R.layout.activity_watchlist);
        init();
    }

    private void init() {
        watchlistVM = new ViewModelProvider(this).get(WatchlistVM.class);
        activityWatchlistBinding.imageBack.setOnClickListener(view -> onBackPressed());
        watchlist = new ArrayList<>();
        loadWatchlist();
    }

    private void loadWatchlist() {
        activityWatchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchlistVM.loadWatchlist().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    activityWatchlistBinding.setIsLoading(false);
                    if (watchlist.size() > 0) {
                        watchlist.clear();
                    }
                    watchlist.addAll(tvShows);
                    watchlistAdapter = new WatchlistAdapter(watchlist, this);
                    activityWatchlistBinding.watchlistRecyclerView.setAdapter(watchlistAdapter);
                    activityWatchlistBinding.watchlistRecyclerView.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();
                })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TempDataHolder.is_wl_updated){
            loadWatchlist();
            TempDataHolder.is_wl_updated =false;
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWL(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();
        compositeDisposableForDelete.add(watchlistVM.removeTVShow(tvShow)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchlist.remove(position);
                    watchlistAdapter.notifyItemRemoved(position);
                    watchlistAdapter.notifyItemRangeChanged(position, watchlistAdapter.getItemCount());
                    compositeDisposableForDelete.dispose();
                })
        );
    }
}