package com.mediaplayer.adapter;

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

    private List<Track> tracks;
    private Track track;
    private Context context;

    public LoadTrackAdapter(Context context, int trackViewId, ArrayList<Track> tracks, Track track) {
        super(context, trackViewId);
        this.tracks = tracks;
        this.track = track;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Track getItem(int i) {
        return tracks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder trackViewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.track_list_item, viewGroup, false);

            trackViewHolder = new ViewHolder();
            trackViewHolder.trackName = (TextView) view.findViewById(R.id.trackName);

            view.setTag(trackViewHolder);
        } else {
            trackViewHolder = (ViewHolder) view.getTag();
        }

        Track track = tracks.get(i);

        if (track.equals(this.track)) {
            trackViewHolder.trackName.setBackground(getContext().getResources().getDrawable(R.drawable.pressed_track_list));
        } else {
            trackViewHolder.trackName.setBackground(null);
        }

        trackViewHolder.trackName.setText(track.getName());
        trackViewHolder.trackName.setTag(track);

        return view;
    }
}