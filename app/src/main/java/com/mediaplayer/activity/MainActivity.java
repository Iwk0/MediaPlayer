package com.mediaplayer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.mediaplayer.R;
import com.mediaplayer.utils.MyTabListener;

public class MainActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Fragment music = new TrackListFragment();
        Fragment playList = new PlayListFragment();

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab musicTab = actionBar.newTab().setText("Music");
        ActionBar.Tab playListTab = actionBar.newTab().setText("Play List");

        musicTab.setTabListener(new MyTabListener(music));
        playListTab.setTabListener(new MyTabListener(playList));

        actionBar.addTab(musicTab);
        actionBar.addTab(playListTab);
    }
}