package com.mediaplayer.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.utils.MusicPlayer;

public class MusicPlayerActivity extends Activity {

    private static final String TRACK_PATH = "TRACK_PATH";
    private static final String TRACK_NAME = "TRACK_NAME";

    private MusicPlayer musicPlayer;
    private ImageButton playButton;
    private SeekBar seekBar;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        extras = getIntent().getExtras();
        if (extras != null) {
            musicPlayer = new MusicPlayer(this, extras.getString(TRACK_PATH), R.id.seekBar, R.id.image, R.id.progressBar);
            ((TextView) findViewById(R.id.trackName)).setText(extras.getString(TRACK_NAME));
        }

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(musicPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    musicPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playButton = (ImageButton) findViewById(R.id.controlButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pause();
                    ((ImageButton) view).setImageResource(R.drawable.play);
                } else {
                    musicPlayer.start();
                    ((ImageButton) view).setImageResource(R.drawable.pause);
                }
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                musicPlayer.stop();
                playButton.setImageResource(R.drawable.play);
            }
        });
    }
}