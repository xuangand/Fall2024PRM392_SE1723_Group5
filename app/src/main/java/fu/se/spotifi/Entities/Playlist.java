package fu.se.spotifi.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "playlist")
public class Playlist implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int Id;
    private String Name;
    private int Thumbnail;

    @Ignore
    private int songCount;

    public Playlist(int id, String name, int thumbnail) {
        Id = id;
        Name = name;
        Thumbnail = thumbnail;
    }

    public Playlist() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }

    public int getSongCount() { return songCount; }

    public void setSongCount(int songCount) {  this.songCount = songCount; }
}
