package com.mediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Image;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.Constants;
import com.mediaplayer.utils.Database;
import com.mediaplayer.utils.ImageResize;
import com.mediaplayer.utils.SaveSettings;
import com.mediaplayer.utils.TimeFormatter;
import com.mediaplayer.utils.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicPlayerActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private List<Track> tracks;
    private List<Track> recentlyPlayedTracks;
    private Image image;
    private Runnable runnable;
    private Handler handler;
    private SaveSettings saveSettings;
    private Database recentlyPlayedTracksTracksRepository;

    //Views
    private SeekBar seekBar;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView currentTimeView, trackDurationView, trackNameView;

    private int trackIndex, shuffleIndex, numberOfImages, oldIndex = -1;
    private double interval;
    private boolean isLooping, shuffleMode, isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        recentlyPlayedTracksTracksRepository = new Database(this);
        saveSettings = new SaveSettings(this);

        shuffleMode = saveSettings.load(Constants.SAVE_RANDOM_MODE, false);
        isLooping = saveSettings.load(Constants.SAVE_LOOPING, true);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.image);
        currentTimeView = (TextView) findViewById(R.id.currentTime);
        trackDurationView = (TextView) findViewById(R.id.songDuration);
        trackNameView = (TextView) findViewById(R.id.trackName);

        //Allow music volume control
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tracks = extras.getParcelableArrayList(Constants.TRACKS_PATH);
            recentlyPlayedTracks = extras.getParcelableArrayList(Constants.RECENTLY_PLAYED);

            Track track = extras.getParcelable(Constants.TRACK);
            String trackName = track.getName();
            trackNameView.setText(trackName);
            trackNameView.setTag(track);
            trackIndex = track.getId();

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(track.getPath());
                mediaPlayer.prepare();
                mediaPlayer.setLooping(isLooping);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        changeTrackNext();
                    }
                });

                image = XmlParser.xmlParserImage(1);
                numberOfImages = image.getPaths().size();
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException", e.getMessage());
            }

            if (isLooping) {
                ((ImageButton) findViewById(R.id.repeat)).setImageResource(R.drawable.repeat_pressed);
            }

            if (shuffleMode) {
                ((ImageButton) findViewById(R.id.randomMode)).setImageResource(R.drawable.random_pressed);

                final int SIZE = recentlyPlayedTracks.size();
                if (SIZE > 0 && !recentlyPlayedTracks.get(SIZE - 1).getName().equals(trackName)) {
                    recentlyPlayedTracksTracksRepository.add(track, Constants.RECENTLY_PLAYED_TABLE_NAME);
                    recentlyPlayedTracks.add(track);
                }

                shuffleIndex = recentlyPlayedTracks.size() - 1;
            }


            handler = new Handler();
            runnable = new Runnable() {

                private int currentPosition;
                private int newIndex;

                @Override
                public void run() {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    newIndex = (int) (currentPosition / interval);

                    if (newIndex != oldIndex && newIndex < numberOfImages) {
                        new AsyncTask<Void, Void, Bitmap>() {

                            @Override
                            protected void onPreExecute() {
                                imageView.setImageBitmap(null);
                                progressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                super.onPostExecute(bitmap);
                                progressBar.setVisibility(View.GONE);
                                imageView.setImageBitmap(bitmap);
                                oldIndex = newIndex;
                            }

                            @Override
                            protected Bitmap doInBackground(Void... voids) {
                                return ImageResize.decodeSampledBitmapFromUri(Constants.STORAGE_PATH + image.getPaths().get(newIndex), 250, 250);
                            }
                        }.execute();
                    }

                    handler.postDelayed(this, 1000);
                }
            };

            final int DURATION = mediaPlayer.getDuration();
            interval = DURATION * 1.0 / numberOfImages;
            trackDurationView.setText(TimeFormatter.format(DURATION));

            seekBar.setMax(DURATION);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        mediaPlayer.seekTo(i);
                    }

                    currentTimeView.setText(TimeFormatter.format(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            mediaPlayer.start();
            handler.post(runnable);

            animationReset();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);

        saveSettings.save(Constants.SAVE_LOOPING, isLooping);
        saveSettings.save(Constants.SAVE_RANDOM_MODE, shuffleMode);
        saveSettings.save(Constants.TRACK, (Track) trackNameView.getTag());

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void changeTrackEvents(View view) {
        int viewId = view.getId();

        if (viewId == R.id.next) {
            changeTrackNext();
        } else if (viewId == R.id.previous) {
            changeTrackPrevious();
        }
    }

    public void clickEvents(View view) {
        int viewId = view.getId();

        if (viewId == R.id.trackName) {
            Intent trackInfoActivity = new Intent(this, TrackInfoActivity.class);
            trackInfoActivity.putExtra(Constants.TRACK, (Track) view.getTag());
            startActivity(trackInfoActivity);
        } else if (viewId == R.id.controlButton) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                ((ImageButton) view).setImageResource(R.drawable.clicked_play);
            } else {
                mediaPlayer.start();
                ((ImageButton) view).setImageResource(R.drawable.clicked_pause);
            }

            isPaused = !mediaPlayer.isPlaying();
        } else if (viewId == R.id.repeat) {
            if (mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
                ((ImageButton) view).setImageResource(R.drawable.repeat);
            } else {
                mediaPlayer.setLooping(true);
                ((ImageButton) view).setImageResource(R.drawable.repeat_pressed);
            }

            isLooping = mediaPlayer.isLooping();
        } else if (viewId == R.id.randomMode) {
            if (shuffleMode) {
                ((ImageButton) view).setImageResource(R.drawable.random);
            } else {
                ((ImageButton) view).setImageResource(R.drawable.random_pressed);

                Track track = tracks.get(trackIndex);
                final int SIZE = recentlyPlayedTracks.size();

                if (SIZE > 0 && !recentlyPlayedTracks.get(SIZE - 1).getName().equals(track.getName())) {
                    recentlyPlayedTracksTracksRepository.add(track, Constants.RECENTLY_PLAYED_TABLE_NAME);
                    recentlyPlayedTracks.add(track);
                    shuffleIndex = recentlyPlayedTracks.size() - 1;
                }
            }

            shuffleMode = !shuffleMode;
        } else if (viewId == R.id.playlist) {
            if (mediaPlayer != null) {
                handler.removeCallbacks(runnable);

                mediaPlayer.release();
                mediaPlayer = null;
            }

            saveSettings.save(Constants.SAVE_LOOPING, isLooping);
            saveSettings.save(Constants.SAVE_RANDOM_MODE, shuffleMode);
            saveSettings.save(Constants.TRACK, (Track) trackNameView.getTag());

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void changeTrackNext() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (shuffleMode) {
                    if (shuffleIndex + 1 < recentlyPlayedTracks.size()) {
                        trackChanger(recentlyPlayedTracks.get(++shuffleIndex));
                    } else {
                        Random random = new Random();
                        trackIndex = random.nextInt(tracks.size());

                        Track track = tracks.get(trackIndex);

                        String trackName = track.getName();
                        final int SIZE = recentlyPlayedTracks.size();

                        while (SIZE > 0 && recentlyPlayedTracks.get(SIZE - 1).getName().equals(trackName)) {
                            trackIndex = random.nextInt(tracks.size());
                            track = tracks.get(trackIndex);
                        }

                        recentlyPlayedTracksTracksRepository.add(track, Constants.RECENTLY_PLAYED_TABLE_NAME);
                        recentlyPlayedTracks.add(track);

                        shuffleIndex = recentlyPlayedTracks.size() - 1;

                        trackChanger(track);
                    }
                } else {
                    trackChanger(tracks.get(++trackIndex));
                }
            }
        }).start();
    }

    private void changeTrackPrevious() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (shuffleMode) {
                    Random random = new Random();

                    if (recentlyPlayedTracks.size() > 1 && shuffleIndex > 0) {
                        File recentlyTrack;
                        Track track;
                        String trackPath;

                        do {
                            track = recentlyPlayedTracks.get(--shuffleIndex);
                            trackPath = track.getPath();
                            recentlyTrack = new File(trackPath);
                        } while (!recentlyTrack.exists() && shuffleIndex > 0);

                        if (trackPath.isEmpty()) {
                            trackIndex = random.nextInt(tracks.size());
                            track = tracks.get(trackIndex);
                        }

                        trackChanger(track);
                    } else {
                        trackIndex = random.nextInt(tracks.size());

                        Track track = tracks.get(trackIndex);
                        String trackName = track.getName();
                        final int SIZE = recentlyPlayedTracks.size();

                        while (SIZE > 0 && recentlyPlayedTracks.get(SIZE - 1).getName().equals(trackName)) {
                            trackIndex = random.nextInt(tracks.size());
                            track = tracks.get(trackIndex);
                        }

                        recentlyPlayedTracksTracksRepository.add(track, Constants.RECENTLY_PLAYED_TABLE_NAME);
                        recentlyPlayedTracks.add(0, track);
                        shuffleIndex = 0;

                        trackChanger(track);
                    }
                } else {
                    if (trackIndex - 1 >= 0) {
                        trackChanger(tracks.get(--trackIndex));
                    }
                }
            }
        }).start();
    }

    private void trackChanger(final Track TRACK) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationReset();

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(TRACK.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(isLooping);
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }

                if (!isPaused) {
                    mediaPlayer.start();
                }

                trackNameView.setText(TRACK.getName());
                trackNameView.setTag(TRACK);

                final int DURATION = mediaPlayer.getDuration();
                interval = DURATION * 1.0 / numberOfImages;

                seekBar.setProgress(0);
                seekBar.setMax(DURATION);

                trackDurationView.setText(TimeFormatter.format(DURATION));
            }
        });
    }

    private void animationReset() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move);
        trackNameView.startAnimation(animation);
    }
}