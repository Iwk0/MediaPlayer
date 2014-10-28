package com.mediaplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mediaplayer.R;
import com.mediaplayer.model.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends ArrayAdapter<Track>  {

    private static ArrayList<Track> checkedTracks;
    private static class ViewHolder {
        CheckBox checkBox;
    }

    private List<Track> tracks;
    private Context context;

    public PlayListAdapter(Context context, int trackViewId, ArrayList<Track> tracks) {
        super(context, trackViewId);
        this.tracks = tracks;
        this.context = context;

        checkedTracks = new ArrayList<Track>();
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
            view = inflater.inflate(R.layout.playlist_item, viewGroup, false);

            trackViewHolder = new ViewHolder();
            trackViewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            trackViewHolder.checkBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Track track = (Track) compoundButton.getTag();
                    track.setSelected(compoundButton.isChecked());

                    if (isChecked) {
                        track.setId(checkedTracks.size());
                        checkedTracks.add(track);
                    } else {
                        checkedTracks.remove(track);
                    }
                }
            });

            view.setTag(trackViewHolder);
        } else {
            trackViewHolder = (ViewHolder) view.getTag();
        }

        Track track = tracks.get(i);
        trackViewHolder.checkBox.setText(track.getName());
        trackViewHolder.checkBox.setTag(tracks.get(i));
        trackViewHolder.checkBox.setChecked(track.isSelected());

        return view;
    }

    public ArrayList<Track> getCheckedTracks() {
        return checkedTracks;
    }
}