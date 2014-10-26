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

/**
 * Created by Ivo Mishev on 25/10/2014.
 */
public class PlayListAdapter extends ArrayAdapter<Track>  {

    private static class ViewHolder {
        CheckBox checkBox;
    }

    private List<Track> tracks;
    private List<Track> checkedTracks;
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
        final ViewHolder trackViewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.playlist_item, viewGroup, false);

            trackViewHolder = new ViewHolder();
            trackViewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            trackViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Track track = (Track) trackViewHolder.checkBox.getTag();
                    track.setSelected(compoundButton.isChecked());

                    if (b) {
                        checkedTracks.add(track);
                    } else {
                        checkedTracks.remove(track);
                    }
                }
            });

            trackViewHolder.checkBox.setTag(tracks.get(i));
            view.setTag(trackViewHolder);
        } else {
            trackViewHolder = (ViewHolder) view.getTag();
            ((ViewHolder) view.getTag()).checkBox.setTag(tracks.get(i));
        }

        Track track = (Track) trackViewHolder.checkBox.getTag();
        trackViewHolder.checkBox.setText(track.getName());
        trackViewHolder.checkBox.setChecked(track.isSelected());

        return view;
    }

    public List<Track> getCheckedTracks() {
        return checkedTracks;
    }
}