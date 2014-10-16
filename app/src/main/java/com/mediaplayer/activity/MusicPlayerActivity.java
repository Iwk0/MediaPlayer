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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Image;
import com.mediaplayer.model.Track;
import com.mediaplayer.utils.ImageResize;
import com.mediaplayer.utils.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends Activity {

    private static final String TRACKS_PATH = "tracks path";
    private static final String TRACK_PATH = "track path";
    private static final String TRACK_NAME = "track name";
    private static final String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private MediaPlayer mediaPlayer;
    private List<Track> tracks;
    private Image image;
    private Runnable runnable;
    private Handler handler;

    //Views
    private SeekBar seekBar;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView currentTime, trackDuration, trackName;

    private int songIndex, numberOfImages, oldIndex = -1;
    private byte changeMode;
    private double interval;
    private boolean isLooping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

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
            tracks = extras.getParcelableArrayList(TRACKS_PATH);
            String path = extras.getString(TRACK_PATH);

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        ((ImageButton) (findViewById(R.id.controlButton))).setImageResource(R.drawable.play);
                        //http://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
                        //Линк за отваряне на галерия със снимки
                        Random random = new Random();

                        switch (changeMode) {
                            case 0 : mediaPlayer.seekTo(0); break;
                            case 1 : songIndex++; break;
                            case 2 : songIndex = random.nextInt(tracks.size()); break;
                        }
                        //TODO fix music mode
                        //TODO hide all events in xml file
                        try {
                            songChanger();
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage());
                        }
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
                    songIndex = index;
                    break;
                }
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
                    currentTime.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))));

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
                                return ImageResize.decodeSampledBitmapFromUri(STORAGE_PATH + image.getPaths().get(newIndex), 250, 250);
                            }
                        }.execute();
                    }

                    handler.postDelayed(this, 1000);
                }
            };

            /*Events*/
            trackName.setText(extras.getString(TRACK_NAME));
            trackName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent trackInfoActivity = new Intent(getApplicationContext(), TrackInfoActivity.class);
                    trackInfoActivity.putExtra("track name", ((TextView) view).getText());
                    startActivity(trackInfoActivity);
                }
            });

            int duration = mediaPlayer.getDuration();
            trackDuration.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            ));

            seekBar.setMax(duration);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        mediaPlayer.seekTo(i);
                    } else {
                        currentTime.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(i),
                                TimeUnit.MILLISECONDS.toSeconds(i) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i))
                        ));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            findViewById(R.id.nextMode).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    changeMode = changeMode == 2 ? changeMode = 0 : ++changeMode;
                }
            });

            findViewById(R.id.controlButton).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        ((ImageButton) view).setImageResource(R.drawable.play);
                    } else {
                        mediaPlayer.start();
                        ((ImageButton) view).setImageResource(R.drawable.pause);
                    }
                }
            });

            findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (songIndex - 1 >= 0) {
                        songIndex--;

                        try {
                            songChanger();
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage());
                        }
                    }
                }
            });

            findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (songIndex + 1 < tracks.size()) {
                        songIndex++;

                        try {
                            songChanger();
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage());
                        }
                    }
                }
            });

            findViewById(R.id.repeat).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isLooping()) {
                        mediaPlayer.setLooping(false);
                    } else {
                        mediaPlayer.setLooping(true);
                    }

                    isLooping = mediaPlayer.isLooping();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
        handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        handler.removeCallbacks(runnable);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void songChanger() throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(tracks.get(songIndex).getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        mediaPlayer.setLooping(isLooping);

        trackName.setText(tracks.get(songIndex).getName());

        int duration = mediaPlayer.getDuration();

        seekBar.setProgress(0);
        seekBar.setMax(duration);

        trackDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));
    }
}