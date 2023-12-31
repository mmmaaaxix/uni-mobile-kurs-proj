package com.maxx.kurs_proj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements FavoritesRecycleViewAdapter.ItemClickListener {

    private FavoritesRecycleViewAdapter _favoritesRecycleViewAdapter;
    private MealDbClient _mealDbClient;
    private EditText _searchBar;
    RecyclerView _recyclerView;
    FavoritesContainer _favoritesContainer;
    List<Meal> _displayedMeals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        _favoritesContainer = new FavoritesContainer(this);

        _mealDbClient = new MealDbClient(this);

        _recyclerView = findViewById(R.id.favoritesRecycler);
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));

        _searchBar = findViewById(R.id.searchBar);
        _searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                OnSearchTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });

        _favoritesRecycleViewAdapter = new FavoritesRecycleViewAdapter(this, _displayedMeals);
        _favoritesRecycleViewAdapter.setClickListener(this);
        _recyclerView.setAdapter(_favoritesRecycleViewAdapter);

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
                                        Utils.ShowErrorToast(FavoritesActivity.this, failReason.toString());
                                        ResetView();
                                    }
                                    @Override
                                    public void onLoadingCancelled(String imageUri, View view) {
                                        Utils.ShowErrorToast(FavoritesActivity.this, "Загрузка изображения отменена");
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
                    Utils.ShowErrorToast(FavoritesActivity.this, error.getMessage());
                    ResetView();
                }
        );
    }

    private void ResetView() {
        UpdateRecycleView(_favoritesContainer.FindByName("", 100));
        _searchBar.setText("");

        _recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.scroll_layout).setVisibility(View.GONE);
        _searchBar.setVisibility(View.VISIBLE);
    }

    private void UpdateRecycleView(List<Meal> mealsToDisplay) {
        _displayedMeals.clear();
        _displayedMeals.addAll(mealsToDisplay);
        _favoritesRecycleViewAdapter.notifyDataSetChanged();
    }

    private void EnterLoadingMode() {
        _recyclerView.setVisibility(View.GONE);
        findViewById(R.id.scroll_layout).setVisibility(View.VISIBLE);
        _searchBar.setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        ResetView();
    }

    private void OnSearchTextChanged() {
        String searchText = _searchBar.getText().toString();

        List<Meal> foundMeals = _favoritesContainer.FindByName(searchText, 100);
        UpdateRecycleView(foundMeals);
    }
}

