package com.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;

import java.util.ArrayList;

public class TrackListActivity extends Activity {

    private static final String TRACK_PATH = "TRACK_PATH";
    private static final String TRACK_NAME = "TRACK_NAME";

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
                        intent.putExtra(TRACK_PATH, (String) textViewItem.getTag());
                        intent.putExtra(TRACK_NAME, (String) textViewItem.getText());
                        startActivity(intent);
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                String[] type = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME };

                Cursor files = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, type, null, null, null);

                while(files.moveToNext()) {
                    String trackName = files.getString(2);
                    tracks.add(new Track(trackName.substring(0, trackName.length() - 4), files.getString(1)));
                }

                return null;
            }
        }.execute();
    }
}