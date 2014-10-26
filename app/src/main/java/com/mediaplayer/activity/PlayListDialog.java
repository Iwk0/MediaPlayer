package com.mediaplayer.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mediaplayer.R;
import com.mediaplayer.adapter.PlayListAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Tracks;

import java.util.ArrayList;

public class PlayListDialog extends Activity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new AsyncTask<Void, Void, ArrayList<Track>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final ArrayList<Track> TRACKS) {
                super.onPostExecute(TRACKS);

                ListView listView = (ListView) findViewById(R.id.trackListView);
                final PlayListAdapter playListAdapter = new PlayListAdapter(PlayListDialog.this, R.layout.playlist_item, TRACKS);
                listView.setAdapter(new PlayListAdapter(PlayListDialog.this, R.layout.playlist_item, TRACKS));

                findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int a = playListAdapter.getCheckedTracks().size();
                        int t = 4343;
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                return Tracks.getAllTracks(PlayListDialog.this);
            }
        }.execute();
    }
}