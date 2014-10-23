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
        String createTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s INTEGER);",
                Constants.TABLE_NAME,
                Constants.COLUMN_ID,
                Constants.COLUMN_POSITION,
                Constants.COLUMN_NAME,
                Constants.COLUMN_PATH,
                Constants.COLUMN_ALBUM,
                Constants.COLUMN_DURATION);
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
        values.put(Constants.COLUMN_POSITION, track.getId());
        values.put(Constants.COLUMN_ALBUM, track.getAlbum());
        values.put(Constants.COLUMN_DURATION, track.getDuration());

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
                Track track = new Track();
                track.setId(cursor.getInt(1));
                track.setName(cursor.getString(2));
                track.setPath(cursor.getString(3));
                track.setAlbum(cursor.getString(4));
                track.setDuration(cursor.getInt(5));
                trackArrayList.add(track);
            } while (cursor.moveToNext());
        }

        return trackArrayList;
    }
}