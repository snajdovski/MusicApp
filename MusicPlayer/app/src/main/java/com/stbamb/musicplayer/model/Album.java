package com.stbamb.musicplayer.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Esteban on 6/10/2016.
 */

public class Album
{
    private String albumName;
    private String band;
    private int releaseDate;
    private int timesSold;

    public String getAlbumName() {
        return albumName;
    }

    public String getBand() {
        return band;
    }

    public int getReleaseDate() {
        return releaseDate;
    }

    public int getTimesSold() {
        return timesSold;
    }

    public void setTimesSold(int timesSold) {
        this.timesSold = timesSold;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("albumName", albumName);
        result.put("band", band);
        result.put("releaseDate", releaseDate);
        result.put("timesSold", timesSold);
        return result;
    }
}
