package fu.se.spotifi.Entities;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title, artist, url, duration;
    private byte thumbnail;

    public Song() {
    }

    public Song(String title, String artist, String url, String duration) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
    }

    @Ignore
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public byte getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte thumbnail) {
        this.thumbnail = thumbnail;
    }
}
