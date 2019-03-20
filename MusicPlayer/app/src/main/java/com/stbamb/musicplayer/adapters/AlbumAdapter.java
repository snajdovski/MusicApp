package com.stbamb.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stbamb.musicplayer.R;
import com.stbamb.musicplayer.model.Album;

import java.util.ArrayList;

/**
 * Created by Esteban on 10/10/2016.
 */

/*
This class is based on this tutorial:
https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
and adapted to the necessities pf this project
*/
public class AlbumAdapter extends ArrayAdapter<Album>
{
    public AlbumAdapter(Context context, ArrayList<Album> albums) {
        super(context, R.layout.album_list_item, albums);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Album album = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.album_list_item, parent, false);
            viewHolder.albumName = (TextView) convertView.findViewById(R.id.tv_album_name);
            viewHolder.bandName = (TextView) convertView.findViewById(R.id.tv_band_name);
            viewHolder.releaseDate = (TextView) convertView.findViewById(R.id.tv_release_date);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.albumName.setText(album.getAlbumName());
        viewHolder.bandName.setText(album.getBand());
        viewHolder.releaseDate.setText(Integer.toString(album.getReleaseDate()));
        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView albumName;
        TextView bandName;
        TextView releaseDate;
    }
}