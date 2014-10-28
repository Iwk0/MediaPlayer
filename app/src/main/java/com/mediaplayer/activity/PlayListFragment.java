package com.mediaplayer.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mediaplayer.R;

public class PlayListFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_play_list, container, false);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.quickPlayList);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PlayListDialog.class));
            }
        });

        return view;
    }
}