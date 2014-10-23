package com.mediaplayer.utils;

import android.os.Environment;

/**
 * Created by imishev on 22.10.2014 г..
 */
public interface Constants {

    int DATABASE_VERSION = 40;
    String TRACKS_PATH = "all tracks";
    String TRACK = "track";
    String SAVE_LOOPING = "looping";
    String SAVE_RANDOM_MODE = "shuffle mode";
    String RECENTLY_PLAYED = "recently played";
    String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    String DATABASE_NAME = "recentlyPlayedTracks";
    String TABLE_NAME = "paths";
    String COLUMN_ID = "id";
    String COLUMN_PATH = "path";
    String COLUMN_NAME = "name";
    String COLUMN_ALBUM = "album";
    String COLUMN_DURATION = "duration";
    String COLUMN_POSITION = "position";
    String PREF_SETTINGS = "preference settings";
    String ID = "id";
    String PATH = "path";
    String IMAGE = "image";
}