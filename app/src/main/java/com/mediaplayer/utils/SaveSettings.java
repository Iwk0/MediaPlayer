package com.mediaplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mediaplayer.model.Track;

public class SaveSettings {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SaveSettings(Context context) {
        this.sp = context.getSharedPreferences(Constants.PREF_SETTINGS, Activity.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean load(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void save(String key, Track track) {
        Gson gson = new Gson();
        editor.putString(key, gson.toJson(track));
        editor.commit();
    }

    public Track load(String key) {
        Gson gson = new Gson();
        String json = sp.getString(key, null);
        return gson.fromJson(json, Track.class);
    }
}