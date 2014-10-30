package com.mediaplayer.utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import com.mediaplayer.model.Track;

import java.util.ArrayList;

public class ExternalStorageContent {

    public static ArrayList<Track> getAllTracks(Activity activity) {
        ArrayList<Track> tracks = new ArrayList<Track>();

        final String[] TYPE = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };

        Cursor files = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TYPE, null, null, MediaStore.Audio.Media.DISPLAY_NAME +  " ASC");

        while (files.moveToNext()) {
            int duration = files.getInt(4);

            if (duration >= 30000) {
                String trackName = files.getString(2);

                Track track = new Track();
                track.setName(trackName.substring(0, trackName.length() - 4));
                track.setPath(files.getString(1));
                track.setAlbum(files.getString(3));
                track.setDuration(duration);
                tracks.add(track);
            }
        }

        files.close();
        return tracks;
    }
}