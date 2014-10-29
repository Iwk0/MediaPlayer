package com.mediaplayer.utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import com.mediaplayer.model.Image1;
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

        int id = 0;
        while (files.moveToNext()) {
            int duration = files.getInt(4);

            if (duration >= 30000) {
                String trackName = files.getString(2);

                Track track = new Track();
                track.setId(id++);
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

    public static ArrayList<Image1> getAllImages(Activity activity) {
        ArrayList<Image1> images = new ArrayList<Image1>();

        final String[] TYPE = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.WIDTH
        };

        Cursor files = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, TYPE, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        int id = 0;
        while (files.moveToNext()) {
            Image1 image1 = new Image1();
            image1.setId(id++);
            image1.setName(files.getString(1));
            image1.setPath(files.getString(2));
            image1.setHeight(files.getDouble(3));
            image1.setWidth(files.getDouble(4));
            images.add(image1);
        }

        files.close();
        return images;
    }
}