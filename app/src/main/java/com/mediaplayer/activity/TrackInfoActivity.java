package com.mediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.mediaplayer.R;

public class TrackInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);

        TextView trackName = (TextView) findViewById(R.id.trackName);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trackName.setText(extras.getString("track name"));
        }
    }
}