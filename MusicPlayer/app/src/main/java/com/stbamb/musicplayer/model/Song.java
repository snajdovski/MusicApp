package com.stbamb.musicplayer.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Esteban on 6/10/2016.
 */

public class Song implements Serializable
{
    private String albumName;
    private String bandName;
    private String length;
    private String songName;
    private int timesPlayed;

    public String getAlbumName() {
        return albumName;
    }

    public String getBandName() {
        return bandName;
    }

    public String getLength() {
        return length;
    }

    public String getSongName() {
        return songName;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("albumName", albumName);
        result.put("bandName", bandName);
        result.put("length", length);
        result.put("songName", songName);
        result.put("timesPlayed", timesPlayed);
        return result;
    }
}
