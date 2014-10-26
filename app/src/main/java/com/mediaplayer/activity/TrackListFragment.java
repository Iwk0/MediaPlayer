package com.mediaplayer.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mediaplayer.R;
import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Tracks;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.RecentlyPlayedTracksRepository;
import com.mediaplayer.utils.SaveSettings;

import java.util.ArrayList;

public class TrackListFragment extends Fragment {

    private ArrayList<Track> recentlyPlayedTracks;
    private RecentlyPlayedTracksRepository recentlyPlayedTracksRepository;
    private Track track;

    private Activity activity;
    private ProgressBar progressBar;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_track_list, container, false);
        activity = getActivity();

        SaveSettings saveSettings = new SaveSettings(activity);
        recentlyPlayedTracksRepository = new RecentlyPlayedTracksRepository(activity);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        track = saveSettings.load(Constants.TRACK);

        new AsyncTask<Void, Void, ArrayList<Track>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final ArrayList<Track> TRACKS) {
                super.onPostExecute(TRACKS);

                ListView listView = (ListView) view.findViewById(R.id.trackListView);
                listView.setAdapter(new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track));
                listView.setSelection(track == null ? -1 : track.getId());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(activity, MusicPlayerActivity.class);
                        intent.putExtra(Constants.TRACK, (Track) adapterView.getItemAtPosition(i));
                        intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayedTracks);
                        intent.putParcelableArrayListExtra(Constants.TRACKS_PATH, TRACKS);

                        startActivity(intent);
                        activity.finish();
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                recentlyPlayedTracks = recentlyPlayedTracksRepository.getAllTracks();
                return Tracks.getAllTracks(activity);
            }
        }.execute();

        return view;
    }
}