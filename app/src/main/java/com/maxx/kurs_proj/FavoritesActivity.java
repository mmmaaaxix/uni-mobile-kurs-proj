package com.maxx.kurs_proj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FavoritesActivity extends AppCompatActivity implements FavoritesRecycleViewAdapter.ItemClickListener {

    private FavoritesRecycleViewAdapter _favoritesRecycleViewAdapter;
    private MealDbClient _mealDbClient;
    private ProgressBar _loadingSpinner;
    RecyclerView _recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        _mealDbClient = new MealDbClient(this);
        _loadingSpinner = findViewById(R.id.loadingSpinnerFavorites);

        _recyclerView = findViewById(R.id.favoritesRecycler);
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ResetView();
    }

    @Override
    public void onItemClick(View view, int position) {
        EnterLoadingMode();

        _mealDbClient.GetMealById(
                _favoritesRecycleViewAdapter.getItem(position).GetId(),
                (Response.Listener<Meal>) meal -> {
                    if (!ImgCache.GetInstance().IsSaved(meal.GetImgUrl())) {
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.loadImage(meal.GetImgUrl(),
                                new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        ImgCache.GetInstance().Add(meal.GetImgUrl(), loadedImage);
                                        Intent intent = new Intent(FavoritesActivity.this, MealActivity.class);
                                        intent.putExtra("MEAL", meal);
                                        startActivity(intent);
                                    }
                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                        ShowToastWithError(failReason.toString());
                                        ResetView();
                                    }
                                    @Override
                                    public void onLoadingCancelled(String imageUri, View view) {
                                        ShowToastWithError("Загрузка изображения отменена");
                                        ResetView();
                                    }
                                });
                    }
                    else {
                        Intent intent = new Intent(FavoritesActivity.this, MealActivity.class);
                        intent.putExtra("MEAL", meal);
                        startActivity(intent);
                    }
                },
                (Response.ErrorListener) error -> {
                    ShowToastWithError(error.getMessage());
                    ResetView();
                }
        );
    }

    private void ResetView() {
        _favoritesRecycleViewAdapter = new FavoritesRecycleViewAdapter(this, FavoritesContainer.GetInstance().GetAll());
        _favoritesRecycleViewAdapter.setClickListener(this);
        _recyclerView.setAdapter(_favoritesRecycleViewAdapter);
        
        _recyclerView.setVisibility(View.VISIBLE);
        _loadingSpinner.setVisibility(View.GONE);
    }

    private void EnterLoadingMode() {
        _recyclerView.setVisibility(View.GONE);
        _loadingSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        ResetView();
    }

    private void ShowToastWithError(String error) {
        Toast.makeText(FavoritesActivity.this, "Ошибка: " + error, (int)5).show();
    }
}

