package com.mediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.mediaplayer.R;
import com.mediaplayer.adapter.ImageAdapter;
import com.mediaplayer.model.Image1;
import com.mediaplayer.utils.ExternalStorageContent;

import java.util.ArrayList;

public class ImageGallery extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        ArrayList<Image1> images = ExternalStorageContent.getAllImages(this);
        GridView gridView = (GridView) findViewById(R.id.gallery);
        gridView.setAdapter(new ImageAdapter(this, R.id.imageId, images));
    }
}