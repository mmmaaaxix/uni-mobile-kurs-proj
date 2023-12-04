package com.maxx.kurs_proj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MainActivity extends AppCompatActivity {
    private MealDbClient _mealDbClient;
    private Button _getRndButton, _openFavoritesButton;
    private ProgressBar _loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mealDbClient = new MealDbClient(MainActivity.this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        _getRndButton = findViewById(R.id.getRandomRecipeBtn);
        _openFavoritesButton = findViewById(R.id.openFavoritesBtn);
        _loadingSpinner = findViewById(R.id.loadingSpinner);

        ResetView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        ResetView();
    }

    public void onGetRandomClick(View view) {
        EnterLoadingMode();

        _mealDbClient.GetRandomMeal(
            (Response.Listener<Meal>) meal -> {
                if (!ImgCache.GetInstance().IsSaved(meal.GetImgUrl())) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.loadImage(meal.GetImgUrl(),
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    ImgCache.GetInstance().Add(meal.GetImgUrl(), loadedImage);
                                    Intent intent = new Intent(MainActivity.this, MealActivity.class);
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
                    Intent intent = new Intent(MainActivity.this, MealActivity.class);
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

    public void onOpenFavoritesClick(View view) {
        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
        startActivity(intent);
    }

    private void ShowToastWithError(String error) {
        Toast.makeText(MainActivity.this, "Ошибка: " + error, (int)5).show();
    }

    private void ResetView() {
        _getRndButton.setVisibility(View.VISIBLE);
        _openFavoritesButton.setVisibility(View.VISIBLE);
        _loadingSpinner.setVisibility(View.GONE);
    }

    private void EnterLoadingMode() {
        _getRndButton.setVisibility(View.GONE);
        _openFavoritesButton.setVisibility(View.GONE);
        _loadingSpinner.setVisibility(View.VISIBLE);
    }
}