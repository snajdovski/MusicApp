package com.stbamb.musicplayer.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stbamb.musicplayer.R;
import com.stbamb.musicplayer.model.Song;

public class MusicActivity extends AppCompatActivity {

    private ImageView albumArt;
    private ImageButton playButton;
    private TextView lblSongName;
    private TextView lblBandName;
    private TextView lblAlbumName;
    private TextView currentTime;
    private TextView totalTime;
    private SeekBar seekBar;
    private Toolbar toolbar;
    private Song currentSong;
    private boolean playButtonActive = false;
    private String key;
    private String albumArtKey = "albumArt/";
    final private String albumArtURL = "gs://music-player-ac103.appspot.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mapUIElementsToVars();
        getExtras();
        getAlbumArtFromFirebase();
        setSongInfo();
        setPlayButtonListener();
        setSeekBarListener();
        setSeekBarTimes();
    }

    // Getting all data passed from the previous activity
    private void getExtras()
    {
        currentSong = (Song) getIntent().getExtras().get("song");
        key = (String) getIntent().getExtras().get("key");
        albumArtKey = albumArtKey + key + ".jpg";
    }

    // This function helps to set the data corresponding to the selected song
    private void setSongInfo() {
        lblSongName.setText(currentSong.getSongName());
        lblBandName.setText(currentSong.getBandName());
        lblAlbumName.setText(currentSong.getAlbumName());
        totalTime.setText(currentSong.getLength());
    }

    // This is just to make things a little bit cleaner
    private void mapUIElementsToVars()
    {
        albumArt = (ImageView) findViewById(R.id.iv_album_art);
        playButton = (ImageButton) findViewById(R.id.button_play_pause);
        lblSongName = (TextView) findViewById(R.id.tv_song_name);
        lblBandName = (TextView) findViewById(R.id.tv_band_name);
        lblAlbumName = (TextView) findViewById(R.id.tv_album_name);
        currentTime = (TextView) findViewById(R.id.tv_current_song_time);
        totalTime = (TextView) findViewById(R.id.tv_total_song_time);
        seekBar = (SeekBar) findViewById(R.id.sb_time_progress);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Simple stuff, just setting a button listener
    private void setPlayButtonListener() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonActive = !playButtonActive;
                if (playButtonActive)
                    playButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                else
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        });
    }

    // More listeners...
    private void setSeekBarListener()
    {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                String stringCurrentTime = fromSecsToString(progress);
                currentTime.setText(stringCurrentTime);
            }
        });
    }

    // Just so the back button works the way it should
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // The name describes it all!
    private void getAlbumArtFromFirebase()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(albumArtURL);
        StorageReference islandRef = storageRef.child(albumArtKey);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                albumArt.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                albumArt.setImageResource(R.drawable.no_art_found);
            }
        });
    }

    // More UI related stuff
    private void setSeekBarTimes()
    {
        String stringSongLength = currentSong.getLength();
        int songLengthInSecs = fromStringToSeconds(stringSongLength);
        System.out.println("Song length: " + songLengthInSecs);
        seekBar.setMax(songLengthInSecs);
    }

    // To make the corresponding conversion so you can see the real progress in the SeekBar
    private int fromStringToSeconds(String time)
    {
        int secs = -1;
        try {
            String[] units = time.split(":");
            int minutes = Integer.parseInt(units[0]);
            int seconds = Integer.parseInt(units[1]);
            secs = 60 * minutes + seconds;
        }
        catch (NumberFormatException n) { }
        return secs;
    }

    // To make the corresponding conversion so you can see the real progress in the SeekBar
    private String fromSecsToString(int secs)
    {
        int minutes = secs / 60;
        int seconds = secs % 60;
        if (seconds > 9)
            return minutes + ":" + seconds;
        return minutes + ":0" + seconds;
    }
}
