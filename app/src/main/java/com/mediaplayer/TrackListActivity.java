package com.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;

import java.io.File;
import java.util.ArrayList;

public class TrackListActivity extends Activity {

    private static final String SONG_PATH = "SONG_PATH";
    private ArrayList<Track> tracks;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                tracks = new ArrayList<Track>();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                ArrayAdapter loadSongAdapter = new LoadTrackAdapter(TrackListActivity.this, R.layout.song_list, tracks);
                ListView listView = (ListView) findViewById(R.id.songListView);
                listView.setAdapter(loadSongAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView textViewItem = (TextView) view.findViewById(R.id.songName);
                        Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                        intent.putExtra(SONG_PATH, (String) textViewItem.getTag());
                        startActivity(intent);
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                getAbsolutePathOfAllSongs(Environment.getExternalStorageDirectory());
                return null;
            }
        }.execute();
    }

    private void getAbsolutePathOfAllSongs(File dir) {
        String extension = ".mp3";

        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    getAbsolutePathOfAllSongs(file);
                } else {
                    if (file.getName().endsWith(extension)) {
                        tracks.add(new Track(file.getName(), file.getAbsolutePath()));
                    }
                }
            }
        }
    }
}