package com.mediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.mediaplayer.utils.ImageResize;
import com.mediaplayer.utils.RecentlyPlayedTracksRepository;
import com.mediaplayer.utils.SaveSettings;
import com.mediaplayer.utils.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private List<Track> tracks;
    private List<Track> recentlyPlayedTracks;
    private Image image;
    private Runnable runnable;
    private Handler handler;
    private SaveSettings saveSettings;
    private RecentlyPlayedTracksRepository recentlyPlayedTracksTracksRepository;

    //Views
    private SeekBar seekBar;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView currentTime, trackDuration, trackName;

    private int trackIndex, shuffleIndex, numberOfImages, oldIndex = -1;
    private double interval;
    private boolean isLooping, shuffleMode, isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        recentlyPlayedTracksTracksRepository = new RecentlyPlayedTracksRepository(this);
        saveSettings = new SaveSettings(this);
        shuffleMode = saveSettings.loadSettings(Constants.SAVE_RANDOM_MODE, false);
        isLooping = saveSettings.loadSettings(Constants.SAVE_LOOPING, true);

        if (isLooping) {
            ((ImageButton) findViewById(R.id.repeat)).setImageResource(R.drawable.repeat_pressed);
        }

        if (shuffleMode) {
            ((ImageButton) findViewById(R.id.randomMode)).setImageResource(R.drawable.random_pressed);
        }

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.image);
        currentTime = (TextView) findViewById(R.id.currentTime);
        trackDuration = (TextView) findViewById(R.id.songDuration);
        trackName = (TextView) findViewById(R.id.trackName);

        //Allow music volume control
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tracks = extras.getParcelableArrayList(Constants.TRACKS_PATH);
            trackName.setText(extras.getString(Constants.TRACK_NAME));
            recentlyPlayedTracks = extras.getParcelableArrayList(Constants.RECENTLY_PLAYED);
            String path = extras.getString(Constants.TRACK_PATH);

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
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
                interval = mediaPlayer.getDuration() * 1.0 / numberOfImages;
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException", e.getMessage());
            }

            final int TRACK_LIST_SIZE = tracks.size();
            for (int index = 0; index < TRACK_LIST_SIZE; index++) {
                if (tracks.get(index).getPath().equals(path)) {
                    trackIndex = index;
                    break;
                }
            }

            if (shuffleMode) {
                Track track = new Track(tracks.get(trackIndex).getName(), tracks.get(trackIndex).getPath());
                final int SIZE = recentlyPlayedTracks.size();

                if (SIZE > 0 && !recentlyPlayedTracks.get(SIZE - 1).getName().equals(track.getName())) {
                    recentlyPlayedTracksTracksRepository.add(track);
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

            int duration = mediaPlayer.getDuration();
            trackDuration.setText(timeFormat(duration));

            seekBar.setMax(duration);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        mediaPlayer.seekTo(i);
                    }

                    currentTime.setText(timeFormat(i));
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

        saveSettings.saveSettings(Constants.SAVE_LOOPING, isLooping);
        saveSettings.saveSettings(Constants.SAVE_RANDOM_MODE, shuffleMode);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void changeTrackNext() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (shuffleMode) {
                    if (shuffleIndex + 1 < recentlyPlayedTracks.size()) {
                        Track track = recentlyPlayedTracks.get(++shuffleIndex);
                        trackChanger(track.getPath(), track.getName());
                    } else {
                        Random random = new Random();
                        trackIndex = random.nextInt(tracks.size());

                        Track track = tracks.get(trackIndex);
                        final int SIZE = recentlyPlayedTracks.size();

                        while (recentlyPlayedTracks.get(SIZE - 1).getName().equals(track.getName())) {
                            trackIndex = random.nextInt(tracks.size());
                            track = tracks.get(trackIndex);
                        }

                        recentlyPlayedTracksTracksRepository.add(track);
                        recentlyPlayedTracks.add(track);

                        shuffleIndex = recentlyPlayedTracks.size() - 1;

                        trackChanger(track.getPath(), track.getName());
                    }
                } else {
                    trackIndex++;
                    trackChanger(tracks.get(trackIndex).getPath(), tracks.get(trackIndex).getName());
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
                        String trackPath, trackName;

                        do {
                            Track track = recentlyPlayedTracks.get(--shuffleIndex);
                            trackPath = track.getPath();
                            trackName = track.getName();
                            recentlyTrack = new File(trackPath);
                        } while (!recentlyTrack.exists() && shuffleIndex > 0);

                        if (trackPath.isEmpty()) {
                            trackIndex = random.nextInt(tracks.size());
                            Track track = tracks.get(trackIndex);
                            trackPath = track.getPath();
                            trackName = track.getName();
                        }

                        trackChanger(trackPath, trackName);
                    } else {
                        trackIndex = random.nextInt(tracks.size());

                        Track track = tracks.get(trackIndex);
                        final int SIZE = recentlyPlayedTracks.size();

                        while (recentlyPlayedTracks.get(SIZE - 1).getName().equals(track.getName())) {
                            trackIndex = random.nextInt(tracks.size());
                            track = tracks.get(trackIndex);
                        }

                        recentlyPlayedTracksTracksRepository.add(track);
                        recentlyPlayedTracks.add(0, track);
                        shuffleIndex = 0;

                        trackChanger(track.getPath(), track.getName());
                    }
                } else {
                    if (trackIndex - 1 >= 0) {
                        Track track = tracks.get(--trackIndex);
                        trackChanger(track.getPath(), track.getName());
                    }
                }
            }
        }).start();
    }

    private void trackChanger(final String PATH, final String TRACK) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationReset();

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(PATH);
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(isLooping);
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }

                if (!isPaused) {
                    mediaPlayer.start();
                }

                trackName.setText(TRACK);

                final int DURATION = mediaPlayer.getDuration();
                interval = DURATION * 1.0 / numberOfImages;

                seekBar.setProgress(0);
                seekBar.setMax(DURATION);

                trackDuration.setText(timeFormat(DURATION));
            }
        });
    }

    private void animationReset() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move);
        trackName.startAnimation(animation);
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
            trackInfoActivity.putExtra("track name", ((TextView) view).getText());
            //trackInfoActivity.putExtra("", trackDuration.getText());
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
                    recentlyPlayedTracksTracksRepository.add(track);
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

            saveSettings.saveSettings(Constants.SAVE_LOOPING, isLooping);
            saveSettings.saveSettings(Constants.SAVE_RANDOM_MODE, shuffleMode);

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}