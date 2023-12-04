package com.maxx.kurs_proj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoritesContainer extends SQLiteOpenHelper {
    private static final String _tableName = "favorites";

    public FavoritesContainer(Context context) {
        super(context, "KURS_PROJ", null, 1);
    }

    public List<Meal> FindByName(String name, Integer limit) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select id, meal_name, instructions, img_url from " + _tableName + " where UPPER(meal_name) like '%"+ name.toUpperCase() + "%' limit ?", new String[]{String.valueOf(limit)});

        ArrayList<Meal> selectedMeals = new ArrayList<>(limit);
        if (c.moveToFirst()){
            do {
                String idColumn = c.getString(0);
                String nameColumn = c.getString(1);
                String instrColumn = c.getString(2);
                String urlColumn = c.getString(3);

                selectedMeals.add(new Meal(Long.parseLong(idColumn), nameColumn, instrColumn, urlColumn));
            } while(c.moveToNext());
        }
        c.close();
        db.close();

        return selectedMeals;
    }

    public boolean AlreadyAdded(Meal meal) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select id from " + _tableName + " where id = ?", new String[] {String.valueOf(meal.GetId())});
        boolean res = c.moveToFirst();
        c.close();
        db.close();
        return res;
    }

    public boolean TryAdd(Meal meal) {
        if (AlreadyAdded(meal)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mealDbObj = MapMealToDbObj(meal);
        long res = db.insert(_tableName, null, mealDbObj);
        db.close();

        return res != -1;
    }

    public boolean TryRemove(Meal meal) {
        if (!AlreadyAdded(meal)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mealDbObj = MapMealToDbObj(meal);
        int res = db.delete(_tableName, "id = ?", new String[]{String.valueOf(meal.GetId())});
        db.close();

        return res > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + _tableName
                + " (id INTEGER PRIMARY KEY, "
                + "meal_name TEXT,"
                + "instructions TEXT,"
                + "img_url TEXT)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + _tableName);
        onCreate(db);
    }

    private ContentValues MapMealToDbObj(Meal meal) {
        ContentValues values = new ContentValues();
        values.put("id", meal.GetId());
        values.put("meal_name", meal.GetName());
        values.put("instructions", meal.GetInstructions());
        values.put("img_url", meal.GetImgUrl());
        return values;
    }
}