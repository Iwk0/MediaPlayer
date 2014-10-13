package com.mediaplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imishev on 10.10.2014 г..
 */
public class LoadTrackAdapter extends ArrayAdapter<Track> {

    private static class ViewHolder {
        TextView trackName;
    }

    private List<Track> trackNames;
    private LayoutInflater inflater;

    public LoadTrackAdapter(Context context, int songViewId, ArrayList<Track> trackNames) {
        super(context, songViewId);
        this.trackNames = trackNames;

        inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return trackNames.size();
    }

    @Override
    public Track getItem(int i) {
        return trackNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder songViewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.song_list, viewGroup, false);

            songViewHolder = new ViewHolder();
            songViewHolder.trackName = (TextView) view.findViewById(R.id.songName);

            view.setTag(songViewHolder);
        } else {
            songViewHolder = (ViewHolder) view.getTag();
        }

        songViewHolder.trackName.setText(trackNames.get(i).getName());
        songViewHolder.trackName.setTag(trackNames.get(i).getPath());

        return view;
    }
}