package com.mediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mediaplayer.R;
import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.Database;
import com.mediaplayer.utils.SaveSettings;

import java.util.ArrayList;

public class PlayListDialog extends Activity {

    private ArrayList<Track> recentlyPlayed;

    private Database database;
    private Track track;

    private Activity activity;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_playlist);

        activity = this;
        SaveSettings saveSettings = new SaveSettings(activity);
        database = new Database(activity);
        track = saveSettings.load(Constants.TRACK);

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
                listView.setAdapter(new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track));
                listView.setSelection(track == null ? -1 : track.getId());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(activity, MusicPlayerActivity.class);
                        intent.putExtra(Constants.TRACK, (Track) adapterView.getItemAtPosition(i));
                        intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayed);
                        intent.putParcelableArrayListExtra(Constants.TRACKS, TRACKS);

                        startActivity(intent);
                        activity.finish();
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                return recentlyPlayed = database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME);
            }
        }.execute();

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(activity, QuickPlayListActivity.class), 1);
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayListDialog.this, MusicPlayerActivity.class);
                intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayed);
                intent.putParcelableArrayListExtra(Constants.TRACKS, database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME));

                startActivity(intent);
                activity.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                new AsyncTask<Void, Void, ArrayList<Track>>() {

                    @Override
                    protected ArrayList<Track> doInBackground(Void... voids) {
                        ArrayList<Track> tracks = data.getParcelableArrayListExtra(Constants.QUICK_PLAY_LIST_DATA);

                        for (Track track : tracks) {
                            database.add(track, Constants.QUICK_LIST_TABLE_NAME);
                        }

                        return database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME);
                    }

                    @Override
                    protected void onPostExecute(final ArrayList<Track> TRACKS) {
                        super.onPostExecute(TRACKS);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                ListView listView = (ListView) findViewById(R.id.trackListView);
                                listView.setAdapter(new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track));
                                listView.setSelection(track == null ? -1 : track.getId());
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Intent intent = new Intent(activity, MusicPlayerActivity.class);
                                        intent.putExtra(Constants.TRACK, (Track) adapterView.getItemAtPosition(i));
                                        intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayed);
                                        intent.putParcelableArrayListExtra(Constants.TRACKS, TRACKS);

                                        startActivity(intent);
                                        activity.finish();
                                    }
                                });
                            }
                        });
                    }
                }.execute();
            }
        }
    }
}