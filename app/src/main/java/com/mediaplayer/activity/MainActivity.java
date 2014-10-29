package com.mediaplayer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import com.mediaplayer.R;
import com.mediaplayer.utils.TabListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Fragment music = new TrackListFragment();
        Fragment playList = new PlayListFragment();

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab musicTab = actionBar.newTab().setText("ALL");
        ActionBar.Tab playListTab = actionBar.newTab().setText("Play List");

        musicTab.setTabListener(new TabListener(music));
        playListTab.setTabListener(new TabListener(playList));

        actionBar.addTab(musicTab);
        actionBar.addTab(playListTab);

        startActivity(new Intent(this, ImageGallery.class));
    }
}