package fu.se.spotifi.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fu.se.spotifi.Entities.Playlist;

@Dao
public interface PlaylistDAO {

    @Insert
    void addPlaylist(Playlist playlist);

    @Update
    void updatePlaylist(Playlist playlist);

    @Delete
    void deletePlaylist(Playlist playlist);

    @Query("SELECT * FROM playlist")
    List<Playlist> loadAllPlaylist();
}
