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
        listView = (ListView) findViewById(R.id.trackListView);

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
                listView.setAdapter(loadTrackAdapter);
                listView.setSelection(track == null ? -1 : TRACKS.indexOf(track));
                listView.setOnItemLongClickListener(new LongClick(TRACKS));
                listView.setOnItemClickListener(new OnClick(TRACKS));

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                recentlyPlayed = database.getAllTracks(Constants.RECENTLY_PLAYED_TABLE_NAME);
                return database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME);
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            new AsyncTask<Void, Void, ArrayList<Track>>() {

                @Override
                protected ArrayList<Track> doInBackground(Void... voids) {
                    ArrayList<Track> tracks = data.getParcelableArrayListExtra(Constants.QUICK_PLAY_LIST_DATA);
                    database.add(tracks, Constants.QUICK_LIST_TABLE_NAME);
                    return database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME);
                }

                @Override
                protected void onPostExecute(final ArrayList<Track> TRACKS) {
                    super.onPostExecute(TRACKS);
                    loadTrackAdapter = new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track);
                    listView.setAdapter(loadTrackAdapter);
                    listView.setOnItemLongClickListener(new LongClick(TRACKS));
                    listView.setOnItemClickListener(new OnClick(TRACKS));
                }
            }.execute();
        }
    }

    public void events(View view) {
        int viewId = view.getId();

        if (viewId == R.id.add) {
            startActivityForResult(new Intent(activity, QuickPlayListActivity.class), 1);
        } else if (viewId == R.id.play) {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayed);
            intent.putParcelableArrayListExtra(Constants.TRACKS,
                    database.getAllTracks(Constants.QUICK_LIST_TABLE_NAME));

            startActivity(intent);
            activity.finish();
        }
    }

    private class OnClick implements AdapterView.OnItemClickListener {

        private ArrayList<Track> tracks;

        private OnClick(ArrayList<Track> tracks) {
            this.tracks = tracks;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(activity, MusicPlayerActivity.class);
            intent.putExtra(Constants.TRACK, (Track) adapterView.getItemAtPosition(i));
            intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayed);
            intent.putParcelableArrayListExtra(Constants.TRACKS, tracks);

            startActivity(intent);
            activity.finish();
        }
    }

    private class LongClick implements AdapterView.OnItemLongClickListener {

        private ArrayList<Track> tracks;

        private LongClick(ArrayList<Track> tracks) {
            this.tracks = tracks;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.delete(Constants.QUICK_LIST_TABLE_NAME,
                                    ((Track) adapterView.getItemAtPosition(i)).getId());

                            tracks.remove(i);
                            loadTrackAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return true;
        }
    }
}