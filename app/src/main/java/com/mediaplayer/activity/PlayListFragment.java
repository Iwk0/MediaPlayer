package com.mediaplayer.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mediaplayer.R;

/**
 * Created by imishev on 21.10.2014 г..
 */
public class PlayListFragment extends Fragment {

    private View view;
    private LinearLayout linearLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_play_list, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.quickPlayList);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PlayListDialog.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}