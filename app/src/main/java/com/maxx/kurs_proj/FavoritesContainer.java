package com.maxx.kurs_proj;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesContainer {
    private static FavoritesContainer _instance = null;
    private static final String _favoritesFileName = "favorites.ser";

    public static FavoritesContainer GetInstance() {
        if (_instance == null) {
            _instance = new FavoritesContainer();
        }
        return _instance;
    }
    public static void Init(Context context) {
        _instance._saveDir = context.getFilesDir();
        _instance._favoritesFile = new File(_instance._saveDir, _favoritesFileName);
        _instance.ReadCacheFromFile();
    }

    private Map<Long, Meal> _cacheMap = null;

    private File _saveDir, _favoritesFile;
    private FavoritesContainer() {}

    public List<Meal> GetAll() {
        return new ArrayList<Meal>(_cacheMap.values());
    }

    public boolean AlreadyAdded(Meal meal) {
        return _cacheMap.containsKey(meal.GetId());
    }

    // Транзакционное добавление в кеш и в файл
    public boolean TryAdd(Meal meal) {
        if (AlreadyAdded(meal)) {
            return false;
        }

        _cacheMap.put(meal.GetId(), meal);

        if(!WriteCacheToFile()) {
            _cacheMap.remove(meal.GetId());
            return false;
        }

        return true;
    }

    // Транзакционное удаление из кеша и из файла
    public boolean TryRemove(Meal meal) {
        if (!AlreadyAdded(meal)) {
            return false;
        }

        Meal removedMeal = _cacheMap.remove(meal.GetId());

        if(!WriteCacheToFile()) {
            _cacheMap.put(removedMeal.GetId(), removedMeal);
            return false;
        }

        return true;
    }

    private boolean WriteCacheToFile() {
        if (!_saveDir.exists()) {
            return false;
        }

        boolean success;
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(_favoritesFile);
            out = new ObjectOutputStream(fos);
            out.writeObject(_cacheMap);
            success = true;
        }  catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (fos != null)
                    fos.flush();
                fos.close();
                if (out != null)
                    out.flush();
                out.close();
            } catch (Exception e) {
                success = false;
            }
        }

        return success;
    }

    private void ReadCacheFromFile() {
        if (!_saveDir.exists()) {
            return;
        }

        if (!_favoritesFile.exists()) {
            _cacheMap = new HashMap<>();
            return;
        }

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(_favoritesFile);
            in = new ObjectInputStream(fis);
            _cacheMap = (Map<Long, Meal> ) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(fis != null) {
                    fis.close();
                }
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}