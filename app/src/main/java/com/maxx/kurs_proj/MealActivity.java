package com.maxx.kurs_proj;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MealActivity extends AppCompatActivity {
    private Meal _meal;

    private TextView _mealNameView, _mealRecipeView;
    private ImageView _mealImg;
    private Button _favBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

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
        _favBtn.setText(FavoritesContainer.GetInstance().AlreadyAdded(_meal) ? "Урать из избранного" : "Добавить в избранное");
    }

    public void onAddRemoveFavBtnClick(View view) {
        FavoritesContainer favorites = FavoritesContainer.GetInstance();
        if (favorites.AlreadyAdded(_meal)) {
            if (!favorites.TryRemove(_meal)) {
                ShowToastWithError("Не удалось удалить из избранного");
            }
            else {
                ShowToastWithInfo("Рецепт успешно удален из избранного");
            }
        }
        else {
            if (!favorites.TryAdd(_meal)) {
                ShowToastWithError("Не удалось добавить в избранное");
            }
            else {
                ShowToastWithInfo("Рецепт успешно добавлен в избранное");
            }
        }

        UpdateFavoriteBtn();
    }

    private void ShowToastWithError(String error) {
        Toast.makeText(MealActivity.this, "Ошибка: " + error, (int)5).show();
    }

    private void ShowToastWithInfo(String info) {
        Toast.makeText(MealActivity.this, info, (int)5).show();
    }
}