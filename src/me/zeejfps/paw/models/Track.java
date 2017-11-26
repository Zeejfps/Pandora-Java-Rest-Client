package me.zeejfps.paw.models;

import com.google.gson.annotations.SerializedName;

public class Track {

    private String amazonUrl;
    private String itunesUrl;
    @SerializedName("audioURL")
    private String audioUrl;
    private String songTitle;
    private String artistName;

    public String getAmazonUrl() {
        return amazonUrl;
    }

    public String getItunesUrl() {
        return itunesUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getArtistName() {
        return artistName;
    }
}
