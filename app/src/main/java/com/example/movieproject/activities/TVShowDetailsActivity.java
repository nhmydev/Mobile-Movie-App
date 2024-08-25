package com.example.movieproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.movieproject.R;
import com.example.movieproject.adapters.EpisodesAdapter;
import com.example.movieproject.adapters.ImageSliderAdapter;
import com.example.movieproject.databinding.ActivityTvshowDetailsBinding;
import com.example.movieproject.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.movieproject.models.TVShow;
import com.example.movieproject.responses.TVShowDetailsResponse;
import com.example.movieproject.responses.TVShowResponse;
import com.example.movieproject.uitilities.TempDataHolder;
import com.example.movieproject.viewmodels.TVShowDetailsVM;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {
    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsVM tvShowDetailsVM;

    private BottomSheetDialog episodeBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;

    private Boolean isInWL = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_tvshow_details);
        init();
    }
    private void init(){
        tvShowDetailsVM = new ViewModelProvider(this).get(TVShowDetailsVM.class);
        activityTvshowDetailsBinding.imgBack.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkInWL();
        getTVShowDetails();
    }

    private void checkInWL(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsVM.getTVShowFromWL(String.valueOf(tvShow.getId()))
                .observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    isInWL = true;
                    activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                    compositeDisposable.dispose();
                })
        );
    }
    private void getTVShowDetails(){
        activityTvshowDetailsBinding.setIsLoading(true);
        String tvShowId =String.valueOf(tvShow.getId());
        tvShowDetailsVM.getTVShowDetails(tvShowId).observe(
                this, TVShowDetailsResponse -> {
                    activityTvshowDetailsBinding.setIsLoading(false);
                    if(TVShowDetailsResponse.getTvShowDetails() != null){
                        if(TVShowDetailsResponse.getTvShowDetails().getPictures() != null){
                            loadImgSlider(TVShowDetailsResponse.getTvShowDetails().getPictures());
                        }
                        activityTvshowDetailsBinding.setTvShowImgURL(
                                TVShowDetailsResponse.getTvShowDetails().getImage_path()
                        );
                        activityTvshowDetailsBinding.imgTVShow.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.setDescription(
                                String.valueOf(HtmlCompat.fromHtml(TVShowDetailsResponse.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY))
                        );
                        activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.textReadmore.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.textReadmore.setOnClickListener(v -> {
                            if(activityTvshowDetailsBinding.textReadmore.getText().toString().equals("Read More")){
                                activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                                activityTvshowDetailsBinding.textReadmore.setText(R.string.read_less);
                            }else {
                                activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                                activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                activityTvshowDetailsBinding.textReadmore.setText(R.string.read_more);
                            }

                        });
                        activityTvshowDetailsBinding.setRating(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        Double.parseDouble(TVShowDetailsResponse.getTvShowDetails().getRating())
                                )
                        );
                        if(TVShowDetailsResponse.getTvShowDetails().getGenres() !=null){
                            activityTvshowDetailsBinding.setGenre(TVShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                        }else {
                            activityTvshowDetailsBinding.setGenre("N/A");
                        }
                        activityTvshowDetailsBinding.setRuntime(TVShowDetailsResponse.getTvShowDetails().getRuntime()+ " Min");
                        activityTvshowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.buttonWebsite.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(TVShowDetailsResponse.getTvShowDetails().getUrl()));
                            startActivity(intent);
                        });
                        activityTvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);

                        activityTvshowDetailsBinding.buttonEpisodes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (episodeBottomSheetDialog == null){
                                    episodeBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                                    layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                            LayoutInflater.from(TVShowDetailsActivity.this),
                                            R.layout.layout_episodes_bottom_sheet,
                                            findViewById(R.id.episodesContainer),false
                                    );
                                    episodeBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                    layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                            new EpisodesAdapter(TVShowDetailsResponse.getTvShowDetails().getEpisodes())
                                    );
                                    layoutEpisodesBottomSheetBinding.textTitle.setText(
                                            String.format("Episodes | %s",tvShow.getName())
                                    );
                                    layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> episodeBottomSheetDialog.dismiss());
                                }
                                FrameLayout frameLayout = episodeBottomSheetDialog.findViewById(
                                        com.google.android.material.R.id.design_bottom_sheet
                                );
                                if (frameLayout !=null){
                                    BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                    bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }
                                episodeBottomSheetDialog.show();
                            }
                        });
                        activityTvshowDetailsBinding.imageWatchlist.setOnClickListener(v -> {
                            CompositeDisposable compositeDisposable =new CompositeDisposable();
                            if(isInWL){
                                compositeDisposable.add(tvShowDetailsVM.removeTVShowFromWL(tvShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() ->{
                                            isInWL =false;
                                            TempDataHolder.is_wl_updated = true;
                                            activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_watchlist);
                                            Toast.makeText(getApplicationContext(),"Removed from watchlist",Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                                );
                            }
                            else {
                                compositeDisposable.add(tvShowDetailsVM.addToWatchlist(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            TempDataHolder.is_wl_updated=true;
                                            activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                                            Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                                );
                            }
                        });
                        activityTvshowDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                        loadBasicTVShowDetails();
                    }
                }
        );
    }
    private void loadImgSlider(String[] sliderImg){
        activityTvshowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImg));
        activityTvshowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFading.setVisibility(View.VISIBLE);
        setUpSlider(sliderImg.length);
        activityTvshowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSlider(position);
            }
        });
    }
    private void setUpSlider(int count){
        ImageView[] imgs = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i=0;i<imgs.length;i++){
            imgs[i] = new ImageView(getApplicationContext());
            imgs[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.bg_slider_inactive
            ));
            imgs[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicators.addView(imgs[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSlider(0);
    }
    private void setCurrentSlider(int position){
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i=0;i<childCount;i++){
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if(i==position){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_slider_active)
                );
            }else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_slider_inactive)
                );
            }
        }
    }

    private void loadBasicTVShowDetails(){
        activityTvshowDetailsBinding.setTvShowName(tvShow.getName());
        activityTvshowDetailsBinding.setNetworkCountry(
                tvShow.getNetwork() + "(" +
                        tvShow.getCountry() + ")"
        );
        activityTvshowDetailsBinding.setStatus(tvShow.getStatus());
        activityTvshowDetailsBinding.setStartDate(tvShow.getStartDate());
        activityTvshowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStarted.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
    }
}