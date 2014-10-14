package com.mediaplayer.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.model.Image;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by imishev on 25.9.2014 г..
 */
public class MusicPlayer {

    private final static String ID = "id";
    private final static String PATH = "path";
    private final static String IMAGE = "image";
    private final static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private MediaPlayer mp;
    private Handler handler;
    private Runnable runnable;
    private Image image;
    private String trackPathLocal;

    //Views
    private SeekBar seekBar;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView currentTime;

    private int oldIndex = -1, imageSize;
    private boolean isStopped;
    private double interval;

    public MusicPlayer(Activity activity, String trackPath) {
        this.mp = new MediaPlayer();
        this.isStopped = true;
        this.trackPathLocal = trackPath;

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop(trackPathLocal);
            }
        });

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            image = xmlParserImage(parser, 1);

            mp.setDataSource(trackPathLocal);
            mp.prepare();

            imageSize = image.getPaths().size();
            interval = mp.getDuration() * 1.0 / imageSize;
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", e.getMessage());
        }

        seekBar = (SeekBar) activity.findViewById(R.id.seekBar);
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        imageView = (ImageView) activity.findViewById(R.id.image);
        currentTime = (TextView) activity.findViewById(R.id.currentTime);

        handler = new Handler();
        runnable = new Runnable() {

            private int currentPosition;
            private int newIndex;

            @Override
            public void run() {
                currentPosition = mp.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                newIndex = (int) (currentPosition / interval);
                currentTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                        TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))
                ));

                if (newIndex != oldIndex && newIndex < imageSize) {
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
    }

    public void start() {
        mp.start();

        if (isStopped) {
            mp.seekTo(seekBar.getProgress());
            handler.post(runnable);
            isStopped = false;
        }
    }

    public void pause() {
        mp.pause();
    }

    public void stop(String trackPath) {
        handler.removeCallbacks(runnable);

        mp.stop();
        mp.reset();

        try {
            mp.setDataSource(trackPath);
            mp.prepare();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }

        interval = mp.getDuration() * 1.0 / imageSize;
        isStopped = true;
        oldIndex = - 1;
        seekBar.setProgress(0);
        currentTime.setText("00:00");
        trackPathLocal = trackPath;
    }

    public void seekTo(int i) {
        if (!isStopped) {
            mp.seekTo(i);
        } else {
            currentTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(i),
                    TimeUnit.MILLISECONDS.toSeconds(i) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i))
            ));
        }
    }

    public void release() {
        handler.removeCallbacks(runnable);
        mp.release();
        mp = null;
    }

    public void setLooping(boolean loop) {
        mp.setLooping(loop);
    }

    public int getDuration() {
        return mp.getDuration();
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public boolean isLooping() {
        return mp.isLooping();
    }

    private Image xmlParserImage(XmlPullParser parser, int imagesId) throws XmlPullParserException, IOException {
        parser.setInput(new FileInputStream(STORAGE_PATH + "/program/" + "images.xml"), null);

        Image image = null;
        String curText = null;
        List<String> paths = new ArrayList<String>();
        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase(IMAGE)) {
                        if (image != null && image.getId() == imagesId) {
                            image.setPaths(paths);
                            return image;
                        }
                        paths.clear();
                        image = new Image();
                    }
                    break;
                case XmlPullParser.TEXT:
                    curText = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase(ID)) {
                        image.setId(Integer.parseInt(curText));
                    } else if (tagName.equalsIgnoreCase(PATH)) {
                        paths.add(curText);
                    }
                    break;
            }

            event = parser.next();
        }

        return null;
    }
}