package com.mediaplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mediaplayer.R;
import com.mediaplayer.model.Image1;
import com.mediaplayer.utils.ImageAsyncLoader;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Image1> {

    private static class ViewHolder {
        ImageView imageView;
    }

    private ArrayList<Image1> images;
    private Context context;

    public ImageAdapter(Context context, int imageView, ArrayList<Image1> images) {
        super(context, imageView);
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Image1 getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageId);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        new ImageAsyncLoader(viewHolder.imageView).execute(images.get(i).getPath());

        return view;
    }
}