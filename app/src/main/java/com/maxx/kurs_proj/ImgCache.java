package com.maxx.kurs_proj;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImgCache {
    private static ImgCache _instance;
    public static ImgCache GetInstance() {
        if (_instance == null) {
            _instance = new ImgCache();
        }
        return _instance;
    }

    private LruCache<String, Bitmap> _memoryCache;

    private ImgCache() {
        _memoryCache = new LruCache<String, Bitmap>(5);
    }

    public boolean IsSaved(String imgUrl) {
        return (_memoryCache.get(imgUrl) != null);
    }

    public Bitmap GetSaved(String imgUrl) {
        return _memoryCache.get(imgUrl);
    }

    public void Add(String imgUrl, Bitmap bitmap) {
        if (_memoryCache.get(imgUrl) != null) {
            return;
        }

        _memoryCache.put(imgUrl, bitmap);
    }
}
