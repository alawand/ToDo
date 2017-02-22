package com.sugarcube.todo.models;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Abed on 2/20/2017.
 */

public class ToDoApplication extends Application {

    private int defaultCategory;

    public int getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(int defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String defaultCat = sharedPreferences.getString("default_category", "0");
        defaultCategory = Integer.parseInt(defaultCat);
    }
}
