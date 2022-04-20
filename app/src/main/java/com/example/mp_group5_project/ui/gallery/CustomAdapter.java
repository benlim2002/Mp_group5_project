package com.example.mp_group5_project.ui.gallery;
import android.content.Context;
import android.util.Log;
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
    public View getView(int i, View view, ViewGroup parent) {
        View vi = view;
        if(vi == null)
            vi = inflter.inflate(R.layout.gallery_gridview, parent, false); // inflate the layout
        if( images[i] == null ) {
            Log.d("Gallery [" + i + "]", "null");
        } else {
            File file = new File(images[i]);
            Log.d("Gallery [" + i + "]", file.getAbsolutePath());
            ImageView icon = (ImageView) vi.findViewById(R.id.icon); // get the reference of ImageView
            icon.setImageURI(FileProvider.getUriForFile(context, "com.example.mp_group5_project", file)); // set logo images
        }
        return vi;
    }
}