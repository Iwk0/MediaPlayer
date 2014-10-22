package com.mediaplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by imishev on 16.10.2014 г..
 */
public class SaveSettings {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SaveSettings(Context context) {
        this.sp = context.getSharedPreferences(Constants.PREF_SETTINGS, Activity.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public void saveSettings(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean loadSettings(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void saveSettings(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int loadSettings(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }
}