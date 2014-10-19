package com.mediaplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imishev on 26.9.2014 г..
 */
public class RecentlyPlayedTracks extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recentlyPlayedTracks";
    private static final String TABLE_PATHS = "paths";
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";

    public RecentlyPlayedTracks(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PATHS + "("
                + KEY_ID + " INTEGER AUTOINCREMENT PRIMARY KEY," + KEY_PATH + " TEXT)";
        database.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PATHS);
        onCreate(database);
    }

    public void add(String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, path);

        db.insert(TABLE_PATHS, null, values);
        db.close();
    }

    public ArrayList<String> getAllTracks() {
        ArrayList<String> contactList = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + TABLE_PATHS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public String getLastPathTrack() {
        Cursor cursor = null;
        String path = "";
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            cursor = db.rawQuery("SELECT * FROM paths ORDER BY id DESC LIMIT 1", null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex("path"));
            }

            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}