package com.stbamb.musicplayer.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.stbamb.musicplayer.R;
import com.stbamb.musicplayer.activities.MusicActivity;
import com.stbamb.musicplayer.adapters.SongAdapter;
import com.stbamb.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopSongsFragment extends Fragment{

    private DatabaseReference mDatabase;
    private ListView songsListView;
    private ListAdapter songsAdapter;
    private ProgressBar progressBar;
    private static ArrayList<Song> songs;
    private static Context mContext;

    public TopSongsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        songs = new ArrayList<>();
        mContext = getContext();
        getSongsFromFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songsListView = (ListView) getView().findViewById(R.id.lv_songs);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressbar_songs_loading);
    }

    // This function finds the song that is being 'played' and increments
    // the number of times it has been played on Firebase
    private void incrementTimesPlayed(Song song)
    {
        String key = song.getSongName() + " " + song.getAlbumName() + " " + song.getBandName();
        key = key.toLowerCase();
        key = key.replace(" ", "-");
        song.setTimesPlayed(song.getTimesPlayed() - 1);
        Map<String, Object> songValues = song.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/song/" + key, songValues);
        mDatabase.updateChildren(childUpdates);
    }

    // Setting some listeners
    private void setSongsListViewListener()
    {
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songs.get(position);
                incrementTimesPlayed(song);
                System.out.println(song.getSongName());
                String albumKey = getAlbumArtKey(song);
                toMusicActivity(song, albumKey);
                String toastMessage = "'" + song.getSongName() + "' is now playing";
                Toast toast = Toast.makeText(getActivity(), toastMessage ,Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    // This takes me to the next activity
    private void toMusicActivity(Song song, String key)
    {
        Intent intent = new Intent(getActivity(), MusicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);
        bundle.putSerializable("key", key);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Retrieves all Song data from Firebase
    private void getSongsFromFirebase()
    {
        Query mQuery = mDatabase.child("song").orderByChild("timesPlayed");
        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                songs.add(dataSnapshot.getValue(Song.class));
                songsAdapter = new SongAdapter(getContext(), songs);
                songsListView.setAdapter(songsAdapter);

                if (dataSnapshot.getChildrenCount() == songs.size()) {
                    progressBar.setVisibility(View.GONE);
                    songsListView.setVisibility(View.VISIBLE);
                    setSongsListViewListener();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // The purpose of this function is to get the album art key
    // so it can be displayed in the MusicActivity
    private String getAlbumArtKey(Song song)
    {
        String key = song.getBandName() + " " + song.getAlbumName();
        key = key.toLowerCase();
        key = key.replace(" ", "-");
        return key;
    }

    // Shows up some info related to the song I clicked on
    public static void showSongInfoDialog(final int position)
    {
        final Builder alertDialogBuilder = new Builder(mContext);
        Song song = songs.get(position);
        String songName = "Song name: " + song.getSongName();
        String bandName = "Band name: " + song.getBandName();
        String albumName = "Album name: " + song.getAlbumName();
        String songLength = "Length: " + song.getLength();
        String timesPlayed = "Times played: " + song.getTimesPlayed() * -1;
        alertDialogBuilder.setTitle(R.string.song_info);
        alertDialogBuilder.setMessage(songName + "\n" + bandName + "\n" + albumName + "\n" +
        songLength + "\n" + timesPlayed);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}