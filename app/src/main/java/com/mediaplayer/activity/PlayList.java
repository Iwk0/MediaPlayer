package com.mediaplayer.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediaplayer.R;

/**
 * Created by imishev on 21.10.2014 г..
 */
public class PlayList extends Fragment {

    private View view;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_play_list, container, false);
        return view;
    }
}