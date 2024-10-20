package fu.se.spotifi.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "songlist", indices = {@Index(value = {"playlistId", "songId"}, unique = true)})
public class SongList {

    @PrimaryKey(autoGenerate = true)
    private int Id;

    private int playlistId;
    private int songId;
    private int order;

    // Constructor used by Room
    public SongList(int id, int playlistId, int songId, int order) {
        Id = id;
        this.playlistId = playlistId;
        this.songId = songId;
        this.order = order;
    }

    // Default constructor used by Room
    public SongList() {
    }

    // Constructor that you might use elsewhere, but Room should ignore
    @Ignore
    public SongList(int playlistId, int songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    // Getters and Setters
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
