package com.example.mp_group5_project.ui.gallery;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.example.mp_group5_project.R;

import java.io.File;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String images[];
    LayoutInflater inflter;
    public CustomAdapter(Context applicationContext, String[] images) {
        this.context = applicationContext;
        this.images = images;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return images.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.gallery_gridview, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageURI(FileProvider.getUriForFile(context, "com.example.mp_group5_project", new File(images[i]))); // set logo images
        return view;
    }
}