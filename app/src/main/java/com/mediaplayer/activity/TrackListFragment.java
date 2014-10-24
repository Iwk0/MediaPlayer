package com.mediaplayer.activity;

import android.app.Activity;
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
    private Activity activity;
    private ProgressBar progressBar;
    private Track track;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_track_list, container, false);

        activity = getActivity();
        SaveSettings saveSettings = new SaveSettings(activity);
        recentlyPlayedTracksRepository = new RecentlyPlayedTracksRepository(activity);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        track = saveSettings.loadSettings(Constants.TRACK);

        new AsyncTask<Void, Void, ArrayList<Track>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final ArrayList<Track> TRACKS) {
                super.onPostExecute(TRACKS);

                ArrayAdapter loadSongAdapter = new LoadTrackAdapter(activity, R.layout.track_list_item, TRACKS, track);
                ListView listView = (ListView) view.findViewById(R.id.trackListView);
                listView.setAdapter(loadSongAdapter);
                listView.setSelection(track == null ? -1 : track.getId());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(activity, MusicPlayerActivity.class);
                        intent.putExtra(Constants.TRACK, (Track) view.findViewById(R.id.trackName).getTag());
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

                ArrayList<Track> tracks = new ArrayList<Track>();

                final String[] TYPE = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DURATION };

                Cursor files = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TYPE, null, null, null);

                int id = 0;
                while(files.moveToNext()) {
                    int duration = files.getInt(4);

                    if (duration >= 30000) {
                        String trackName = files.getString(2);

                        Track track = new Track();
                        track.setId(id++);
                        track.setName(trackName.substring(0, trackName.length() - 4));
                        track.setPath(files.getString(1));
                        track.setAlbum(files.getString(3));
                        track.setDuration(duration);
                        tracks.add(track);
                    }
                }

                return tracks;
            }
        }.execute();

        return view;
    }
}