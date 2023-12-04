package com.maxx.kurs_proj;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static void ShowErrorToast(Context context, String error) {
        Toast.makeText(context, "Ошибка: " + error, Toast.LENGTH_LONG).show();
    }

    public static void ShowNotificationToast(Context context, String notification) {
        Toast.makeText(context, notification, Toast.LENGTH_SHORT).show();
    }
}
