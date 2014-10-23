package com.mediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;

import java.util.concurrent.TimeUnit;

public class TrackInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);

        TextView trackName = (TextView) findViewById(R.id.trackNameView);
        TextView trackDuration = (TextView) findViewById(R.id.trackDurationView);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Track track = extras.getParcelable(Constants.TRACK);
            trackName.setText(trackName.getText() + " " + track.getName());
            trackDuration.setText(trackDuration.getText() + " " + timeFormat(track.getDuration()));
        }

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private String timeFormat(int duration) {
        if (TimeUnit.MILLISECONDS.toHours(duration) > 0) {
            return String.format("%2d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        } else {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
    }
}