package com.stbamb.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stbamb.musicplayer.R;
import com.stbamb.musicplayer.fragments.TopSongsFragment;
import com.stbamb.musicplayer.model.Song;

import java.util.ArrayList;

/*
This class is based on this tutorial:
https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
and adapted to the necessities pf this project
*/
public class SongAdapter extends ArrayAdapter<Song>
{
    public SongAdapter(Context context, ArrayList<Song> songs) {
        super(context, R.layout.song_list_item, songs);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Song song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.song_list_item, parent, false);
            viewHolder.songName = (TextView) convertView.findViewById(R.id.tvSong_name);
            viewHolder.bandName = (TextView) convertView.findViewById(R.id.tvBand_name);
            viewHolder.albumName = (TextView) convertView.findViewById(R.id.tvAlbum_name);
            viewHolder.infoButton = (ImageButton) convertView.findViewById(R.id.button_info);
            viewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation scale = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                    v.startAnimation(scale);
                    TopSongsFragment.showSongInfoDialog(position);
                }
            });

            viewHolder.infoButton.setTag(position);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.songName.setText(song.getSongName());
        viewHolder.bandName.setText(song.getBandName());
        viewHolder.albumName.setText(song.getAlbumName());
        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView songName;
        TextView bandName;
        TextView albumName;
        ImageButton infoButton;
    }
}