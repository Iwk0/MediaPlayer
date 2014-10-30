package com.mediaplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mediaplayer.R;
import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.Database;
import com.mediaplayer.utils.SaveSettings;

import java.util.ArrayList;

public class PlayListDialogActivity extends Activity {

    private ArrayList<Track> recentlyPlayed;

    private LoadTrackAdapter loadTrackAdapter;
    private Database database;
    private Track track;

    private Activity activity;
    private ProgressBar progressBar;
    private ListView listView;


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

                loadTrackAdapter = new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track);
                listView = (ListView) findViewById(R.id.trackListView);
                listView.setAdapter(loadTrackAdapter);
                listView.setSelection(track == null ? -1 : TRACKS.indexOf(track));
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                        new AlertDialog.Builder(activity)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.delete)
                                .setMessage(R.string.confirm_message)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        database.delete(Constants.QUICK_LIST_TABLE_NAME, ((Track) adapterView.getItemAtPosition(i)).getId());

                                        TRACKS.remove(i);
                                        loadTrackAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();

                        return true;
                    }
                });
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
                recentlyPlayed = database.getAllTracks(Constants.RECENTLY_PLAYED_TABLE_NAME);
                return database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME);
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
                Intent intent = new Intent(PlayListDialogActivity.this, MusicPlayerActivity.class);
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
                        loadTrackAdapter = new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track);
                        listView.setAdapter(loadTrackAdapter);
                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                            @Override
                            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                                new AlertDialog.Builder(activity)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(R.string.delete)
                                        .setMessage(R.string.confirm_message)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                database.delete(Constants.QUICK_LIST_TABLE_NAME, ((Track) adapterView.getItemAtPosition(i)).getId());

                                                TRACKS.remove(i);
                                                loadTrackAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();

                                return true;
                            }
                        });
                    }
                }.execute();
            }
        }
    }

    private class LongClick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //database.delete(Constants.QUICK_LIST_TABLE_NAME, ((Track) adapterView.getItemAtPosition(i)).getId());

                            //TRACKS.remove(i);
                            loadTrackAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return true;
        }
    }
}