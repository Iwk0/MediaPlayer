package com.mediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mediaplayer.R;
import com.mediaplayer.adapter.PlayListAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.ExternalStorageContent;

import java.util.ArrayList;

public class QuickPlayListActivity extends Activity {

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

                final PlayListAdapter playListAdapter = new PlayListAdapter(QuickPlayListActivity.this, R.layout.playlist_item, TRACKS);

                ((ListView) findViewById(R.id.trackListView)).setAdapter(playListAdapter);

                findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(Constants.QUICK_PLAY_LIST_DATA, playListAdapter.getCheckedTracks());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                return ExternalStorageContent.getAllTracks(QuickPlayListActivity.this);
            }
        }.execute();
    }
}