package com.mediaplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mediaplayer.model.Track;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTableRecentlyPlayed = String.format(Constants.DATABASE,
                Constants.RECENTLY_PLAYED_TABLE_NAME,
                Constants.COLUMN_ID,
                Constants.COLUMN_NAME,
                Constants.COLUMN_PATH,
                Constants.COLUMN_ALBUM,
                Constants.COLUMN_DURATION);

        String createTableQuickPlayList = String.format(Constants.DATABASE,
                Constants.QUICK_LIST_TABLE_NAME,
                Constants.COLUMN_ID,
                Constants.COLUMN_NAME,
                Constants.COLUMN_PATH,
                Constants.COLUMN_ALBUM,
                Constants.COLUMN_DURATION);

        database.execSQL(createTableRecentlyPlayed);
        database.execSQL(createTableQuickPlayList);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + Constants.RECENTLY_PLAYED_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.QUICK_LIST_TABLE_NAME);
        onCreate(database);
    }

    public void add(Track track, String table) {
        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_NAME, track.getName());
        values.put(Constants.COLUMN_PATH, track.getPath());
        values.put(Constants.COLUMN_ALBUM, track.getAlbum());
        values.put(Constants.COLUMN_DURATION, track.getDuration());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(table, null, values);
        db.close();
    }

    public void add(ArrayList<Track> tracks, String table) {
        for (Track track : tracks) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Constants.COLUMN_NAME, track.getName());
            values.put(Constants.COLUMN_PATH, track.getPath());
            values.put(Constants.COLUMN_ALBUM, track.getAlbum());
            values.put(Constants.COLUMN_DURATION, track.getDuration());

            db.insert(table, null, values);
            db.close();
        }
    }

    public boolean delete(String table, int id) {
        return getWritableDatabase().delete(table, Constants.ID + "=" + id, null) > 0;
    }

    public ArrayList<Track> getAllTracks(String table) {
        ArrayList<Track> trackArrayList = new ArrayList<Track>();

        Cursor cursor = getWritableDatabase().
                rawQuery(String.format("SELECT * FROM %s", table), null);

        if (cursor.moveToFirst()) {
            do {
                Track track = new Track();
                track.setId(cursor.getInt(0));
                track.setName(cursor.getString(1));
                track.setPath(cursor.getString(2));
                track.setAlbum(cursor.getString(3));
                track.setDuration(cursor.getInt(4));
                trackArrayList.add(track);
            } while (cursor.moveToNext());
        }

        return trackArrayList;
    }
}