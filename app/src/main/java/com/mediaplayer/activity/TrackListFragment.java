package com.mediaplayer.activity;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.adapter.LoadTrackAdapter;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.RecentlyPlayedTracksRepository;
import com.mediaplayer.utils.SaveSettings;

import java.util.ArrayList;

public class TrackListFragment extends Fragment {

    private ArrayList<Track> recentlyPlayedTracks;
    private RecentlyPlayedTracksRepository recentlyPlayedTracksRepository;
    private ProgressBar progressBar;
    private SaveSettings saveSettings;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_track_list, container, false);

        recentlyPlayedTracksRepository = new RecentlyPlayedTracksRepository(getActivity());
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        saveSettings = new SaveSettings(getActivity());

        new AsyncTask<Void, Void, ArrayList<Track>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final ArrayList<Track> tracks) {
                super.onPostExecute(tracks);

                ArrayAdapter loadSongAdapter = new LoadTrackAdapter(getActivity(), R.layout.track_list_item, tracks);
                ListView listView = (ListView) view.findViewById(R.id.trackListView);
                listView.setAdapter(loadSongAdapter);
                listView.setSelection(saveSettings.loadSettings(Constants.REDIRECT_IN_LIST_VIEW, -1));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView textViewItem = (TextView) view.findViewById(R.id.trackName);

                        Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                        intent.putExtra(Constants.TRACK_NAME, (String) textViewItem.getText());
                        intent.putExtra(Constants.TRACK_PATH, (String) textViewItem.getTag());
                        intent.putParcelableArrayListExtra(Constants.RECENTLY_PLAYED, recentlyPlayedTracks);
                        intent.putParcelableArrayListExtra(Constants.TRACKS_PATH, tracks);

                        saveSettings.saveSettings(Constants.REDIRECT_IN_LIST_VIEW, i);

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected ArrayList<Track> doInBackground(Void... voids) {
                recentlyPlayedTracks = recentlyPlayedTracksRepository.getAllTracks();

                ArrayList<Track> tracks = new ArrayList<Track>();

                String[] type = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME };

                Cursor files = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, type, null, null, null);

                while(files.moveToNext()) {
                    String trackName = files.getString(2);
                    tracks.add(new Track(trackName.substring(0, trackName.length() - 4), files.getString(1)));
                }

                return tracks;
            }
        }.execute();

        return view;
    }
}