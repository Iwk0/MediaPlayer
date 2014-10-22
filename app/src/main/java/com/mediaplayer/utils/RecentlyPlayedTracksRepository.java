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

    public RecentlyPlayedTracksRepository(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL);",
                Constants.TABLE_NAME,
                Constants.COLUMN_ID,
                Constants.COLUMN_NAME,
                Constants.COLUMN_PATH);
        database.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(database);
    }

    public void add(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NAME, track.getName());
        values.put(Constants.COLUMN_PATH, track.getPath());

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Track> getAllTracks() {
        ArrayList<Track> trackArrayList = new ArrayList<Track>();

        String selectQuery = String.format("SELECT * FROM %s", Constants.TABLE_NAME);

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