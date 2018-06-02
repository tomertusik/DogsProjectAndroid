package com.example.tomer.dogsproject;

import android.content.Context;

/**
 * Created by Tomer on 01/06/2018.
 */

public class DogsApp {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DogsApp.context = context;
    }
}
