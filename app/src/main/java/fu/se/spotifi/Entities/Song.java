package fu.se.spotifi.Entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title, artist, url, duration;
    private int thumbnail;

    public Song(String title, String artist, String url, String duration) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
    }

    public Song(String title, String artist, String url) {
        this.title = title;
        this.artist = artist;
        this.url = url;
    }

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

    public void setPath(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
