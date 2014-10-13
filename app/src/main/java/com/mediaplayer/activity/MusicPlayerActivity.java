package com.mediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.MusicPlayer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends Activity {

    private static final String TRACKS_PATH = "TRACKS_PATH";
    private static final String TRACK_PATH = "TRACK_PATH";
    private static final String TRACK_NAME = "TRACK_NAME";

    private ArrayList<Track> tracks;
    private MusicPlayer musicPlayer;
    private ImageButton playButton;
    private TextView trackName, songDuration;
    private SeekBar seekBar;
    private Bundle extras;

    private int songIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        extras = getIntent().getExtras();
        if (extras != null) {
            tracks = extras.getParcelableArrayList(TRACKS_PATH);
            String path = extras.getString(TRACK_PATH);

            musicPlayer = new MusicPlayer(this, path);
            musicPlayer.start();

            trackName = (TextView) findViewById(R.id.trackName);
            trackName.setText(extras.getString(TRACK_NAME));
            trackName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent trackInfoActivity = new Intent(MusicPlayerActivity.this, TrackInfoActivity.class);
                    trackInfoActivity.putExtra("track name", trackName.getText());
                    startActivity(trackInfoActivity);
                }
            });

            final int TRACK_SIZE = tracks.size();
            for (int index = 0; index < TRACK_SIZE; index++) {
                if (tracks.get(index).getPath().equals(path)) {
                    songIndex = index;
                    break;
                }
            }
        }

        int duration = musicPlayer.getDuration();
        songDuration = (TextView) findViewById(R.id.songDuration);
        songDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(duration);
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
                musicPlayer.stop(tracks.get(songIndex).getPath());
                playButton.setImageResource(R.drawable.play);
            }
        });

        findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (songIndex - 1 >= 0) {
                    songIndex--;
                    trackName.setText(tracks.get(songIndex).getName());
                    musicPlayer.stop(tracks.get(songIndex).getPath());

                    int duration = musicPlayer.getDuration();

                    seekBar.setMax(duration);
                    musicPlayer.start();

                    songDuration.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    ));
                }
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (songIndex + 1 < tracks.size()) {
                    songIndex++;
                    trackName.setText(tracks.get(songIndex).getName());
                    musicPlayer.stop(tracks.get(songIndex).getPath());

                    int duration = musicPlayer.getDuration();

                    seekBar.setMax(duration);
                    musicPlayer.start();

                    songDuration.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    ));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        musicPlayer.release();
    }
}