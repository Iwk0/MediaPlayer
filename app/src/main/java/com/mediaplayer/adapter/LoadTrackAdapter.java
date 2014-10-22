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
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.SaveSettings;

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
    private SaveSettings saveSettings;
    private int lastClickedView;

    public LoadTrackAdapter(Context context, int songViewId, ArrayList<Track> trackNames) {
        super(context, songViewId);
        this.trackNames = trackNames;
        this.saveSettings = new SaveSettings(context);

        this.lastClickedView = saveSettings.loadSettings(Constants.REDIRECT_IN_LIST_VIEW, -1);

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
        ViewHolder trackViewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.track_list_item, viewGroup, false);

            trackViewHolder = new ViewHolder();
            trackViewHolder.trackName = (TextView) view.findViewById(R.id.trackName);

            view.setTag(trackViewHolder);
        } else {
            trackViewHolder = (ViewHolder) view.getTag();
        }

        trackViewHolder.trackName.setText(trackNames.get(i).getName());
        trackViewHolder.trackName.setTag(trackNames.get(i).getPath());

        if (lastClickedView == i) {
            view.setBackground(getContext().getResources().getDrawable(R.drawable.controls_background));
        } else {
            view.setBackground(null);
        }

        return view;
    }
}