package com.maxx.kurs_proj;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MealActivity extends AppCompatActivity {
    private Meal _meal;
    private TextView _mealNameView, _mealRecipeView;
    private ImageView _mealImg;
    private Button _favBtn;
    private FavoritesContainer _favoritesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        _favoritesContainer = new FavoritesContainer(this);

        _meal = (Meal) getIntent().getSerializableExtra("MEAL");
        _mealNameView = findViewById(R.id.mealNameView);
        _mealRecipeView = findViewById(R.id.mealRecipeView);
        _mealImg = findViewById(R.id.mealImg);

        _mealNameView.setText(_meal.GetName());
        _mealRecipeView.setText(_meal.GetInstructions());

        _favBtn = findViewById(R.id.addRemoveFavBtn);
        UpdateFavoriteBtn();

        _mealImg.setImageBitmap(ImgCache.GetInstance().GetSaved(_meal.GetImgUrl()));
    }

    private void UpdateFavoriteBtn() {
        _favBtn.setText(_favoritesContainer.AlreadyAdded(_meal) ? "Убрать из избранного" : "Добавить в избранное");
    }

    public void onAddRemoveFavBtnClick(View view) {
        if (_favoritesContainer.AlreadyAdded(_meal)) {
            if (!_favoritesContainer.TryRemove(_meal)) {
                Utils.ShowErrorToast(MealActivity.this, "Не удалось удалить из избранного");
            }
            else {
                Utils.ShowNotificationToast(MealActivity.this, "Рецепт успешно удален из избранного");
            }
        }
        else {
            if (!_favoritesContainer.TryAdd(_meal)) {
                Utils.ShowErrorToast(MealActivity.this, "Не удалось добавить в избранное");
            }
            else {
                Utils.ShowNotificationToast(MealActivity.this, "Рецепт успешно добавлен в избранное");
            }
        }

        UpdateFavoriteBtn();
    }
}