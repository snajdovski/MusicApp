package com.stbamb.musicplayer.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import com.stbamb.musicplayer.adapters.AlbumAdapter;
import com.stbamb.musicplayer.model.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopAlbumsFragment extends Fragment
{
    private DatabaseReference mDatabase;
    private ListView albumsListView;
    private ProgressBar progressBar;
    private ListAdapter albumsAdapter;
    private ArrayList<Album> albums;

    public TopAlbumsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        albums = new ArrayList<>();
        getAlbumsFromFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_albums, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumsListView = (ListView) getView().findViewById(R.id.lv_albums);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressbar_albums_loading);
    }

    // Setting up some listeners
    private void setAlbumsListViewListener()
    {
        albumsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlbumInfoDialog(position);
            }
        });
    }

    // This function finds the album that is being 'purchased' and increments
    // the number of times it has been sold on Firebase
    private void incrementTimesSold(Album album)
    {
        String key = getAlbumKey(album);
        album.setTimesSold(album.getTimesSold() - 1);
        Map<String, Object> albumValues = album.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/album/" + key, albumValues);
        mDatabase.updateChildren(childUpdates);
    }

    // Retrieves all Album data from Firebase
    private void getAlbumsFromFirebase()
    {
        Query mQuery = mDatabase.child("album").orderByChild("timesSold");
        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                albums.add(dataSnapshot.getValue(Album.class));
                albumsAdapter = new AlbumAdapter(getContext(), albums);
                albumsListView.setAdapter(albumsAdapter);

                if (dataSnapshot.getChildrenCount() == albums.size()) {
                    progressBar.setVisibility(View.GONE);
                    albumsListView.setVisibility(View.VISIBLE);
                    setAlbumsListViewListener();
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

    // The purpose of this function is to get the album key
    // so it can be properly incremented when an album is 'purchased'
    private String getAlbumKey(Album album)
    {
        String key = album.getAlbumName() + " " + album.getBand() + " " + album.getReleaseDate();
        key = key.toLowerCase();
        key = key.replace(" ", "-");
        return key;
    }

    // Shows up some info related to the album I clicked on
    private void showAlbumInfoDialog(final int position)
    {
        final Builder alertDialogBuilder = new Builder(getActivity());
        final Album album = albums.get(position);
        final String albumName = "Album name: " + album.getAlbumName();
        String band = "Band name: " + album.getBand();
        String releaseDate = "Release date: " + album.getReleaseDate();
        String timesSold = "Times sold: " + album.getTimesSold() * -1;
        alertDialogBuilder.setTitle(R.string.album_info);
        alertDialogBuilder.setMessage(albumName + "\n" + band + "\n" + releaseDate + "\n" + timesSold);

        alertDialogBuilder.setPositiveButton("I want to buy it now!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        incrementTimesSold(album);
                        String toastMessage = "You have successfully bought " + "'" +
                                album.getAlbumName() + "'";
                        Toast toast = Toast.makeText(getActivity(), toastMessage ,Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}