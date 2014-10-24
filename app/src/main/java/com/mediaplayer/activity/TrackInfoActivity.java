package com.mediaplayer.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.TimeFormatter;

import java.util.concurrent.TimeUnit;

public class TrackInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        TextView trackName = (TextView) findViewById(R.id.trackNameView);
        TextView trackDuration = (TextView) findViewById(R.id.trackDurationView);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Track track = extras.getParcelable(Constants.TRACK);
            trackName.setText(trackName.getText() + " " + track.getName());
            trackDuration.setText(trackDuration.getText() + " " + TimeFormatter.format(track.getDuration()));
        }

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}