package com.mediaplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mediaplayer.model.Track;

import java.util.ArrayList;

/**
 * Created by imishev on 26.9.2014 г..
 */
public class RecentlyPlayedTracksRepository extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 29;
    private static final String DATABASE_NAME = "recentlyPlayedTracks";
    private static final String TABLE_NAME = "paths";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_NAME = "name";

    public RecentlyPlayedTracksRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_CONTACTS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL);",
                TABLE_NAME,
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_PATH);
        database.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void add(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, track.getName());
        values.put(COLUMN_PATH, track.getPath());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Track> getAllTracks() {
        ArrayList<Track> trackArrayList = new ArrayList<Track>();

        String selectQuery = String.format("SELECT * FROM %s", TABLE_NAME);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                trackArrayList.add(new Track(cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        return trackArrayList;
    }
}