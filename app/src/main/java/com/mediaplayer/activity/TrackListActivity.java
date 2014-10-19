package com.mediaplayer.activity;

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

import com.mediaplayer.R;
import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.RecentlyPlayedTracks;

import java.util.ArrayList;
import java.util.List;

public class TrackListActivity extends Activity {

    private static final String TRACKS_PATH = "tracks path";
    private static final String TRACK_PATH = "track path";
    private static final String TRACK_NAME = "track name";
    private static final String RECENTLY_PLAYED = "recently played";

    private ArrayList<String> recentlyPlayed;
    private RecentlyPlayedTracks recentlyPlayedTracks;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        recentlyPlayedTracks = new RecentlyPlayedTracks(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new AsyncTask<Void, Void, ArrayList<Track>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final ArrayList<Track> tracks) {
                super.onPostExecute(tracks);

                ArrayAdapter loadSongAdapter = new LoadTrackAdapter(TrackListActivity.this, R.layout.track_list, tracks);
                ListView listView = (ListView) findViewById(R.id.songListView);
                listView.setAdapter(loadSongAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView textViewItem = (TextView) view.findViewById(R.id.songName);

                        Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                        intent.putExtra(TRACK_NAME, (String) textViewItem.getText());
                        intent.putExtra(TRACK_PATH, (String) textViewItem.getTag());
                        intent.putStringArrayListExtra(RECENTLY_PLAYED, recentlyPlayed);
                        intent.putParcelableArrayListExtra(TRACKS_PATH, tracks);

                        startActivity(intent);
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                recentlyPlayed = recentlyPlayedTracks.getAllTracks();

                ArrayList<Track> tracks = new ArrayList<Track>();

                String[] type = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME };

                Cursor files = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, type, null, null, null);

                while(files.moveToNext()) {
                    String trackName = files.getString(2);
                    tracks.add(new Track(trackName.substring(0, trackName.length() - 4), files.getString(1)));
                }

                return tracks;
            }
        }.execute();
    }
}