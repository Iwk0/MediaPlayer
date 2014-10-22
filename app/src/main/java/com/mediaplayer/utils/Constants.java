package com.mediaplayer.utils;

import android.os.Environment;

/**
 * Created by imishev on 22.10.2014 г..
 */
public interface Constants {

    String TRACKS_PATH = "tracks path";
    String TRACK_PATH = "track path";
    String TRACK_NAME = "track name";
    String SAVE_LOOPING = "looping";
    String SAVE_RANDOM_MODE = "random mode";
    String RECENTLY_PLAYED = "recently played";
    String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    int DATABASE_VERSION = 30;
    String DATABASE_NAME = "recentlyPlayedTracks";
    String TABLE_NAME = "paths";
    String COLUMN_ID = "id";
    String COLUMN_PATH = "path";
    String COLUMN_NAME = "name";

    String PREF_SETTINGS = "preference settings";

    String ID = "id";
    String PATH = "path";
    String IMAGE = "image";

    String REDIRECT_IN_LIST_VIEW = "redirect list view";
}