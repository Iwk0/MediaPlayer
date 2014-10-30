package com.mediaplayer.utils;

import android.os.Environment;

public interface Constants {

    int DATABASE_VERSION = 51;
    String TRACKS = "all tracks";
    String TRACK = "track";
    String LOOPING_MODE = "looping";
    String SHUFFLE_MODE = "shuffle mode";
    String RECENTLY_PLAYED = "recently played";
    String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    String DATABASE = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s INTEGER);";
    String DATABASE_NAME = "Tracks";
    String RECENTLY_PLAYED_TABLE_NAME = "paths";
    String QUICK_LIST_TABLE_NAME = "quickList";
    String COLUMN_ID = "id";
    String COLUMN_PATH = "path";
    String COLUMN_NAME = "name";
    String COLUMN_ALBUM = "album";
    String COLUMN_DURATION = "duration";
    String PREF_SETTINGS = "preference settings";
    String ID = "id";
    String PATH = "path";
    String IMAGE = "image";
    String QUICK_PLAY_LIST_DATA = "quick list";
}