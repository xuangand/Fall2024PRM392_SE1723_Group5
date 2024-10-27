package fu.se.spotifi.Entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "songs")
public class Song implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title, artist, url, duration, thumbnail;

    @Ignore
    public Song() {
    }

    public Song(String title, String artist, String url, String duration, String thumbnail) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
        this.thumbnail = thumbnail;
    }
    @Ignore
    public Song(@NonNull int id, String title, String artist, String url) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
    }

    @Ignore
    public Song(String title, String artist, String url) {
        this.title = title;
        this.artist = artist;
        this.url = url;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    @Override
    public String toString(){
        return title + " - " + artist;
    }
}
