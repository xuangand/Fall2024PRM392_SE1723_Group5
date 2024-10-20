package fu.se.spotifi.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.SongList;

@Dao
public interface PlaylistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPlaylist(Playlist playlist);

    @Update
    void updatePlaylist(Playlist playlist);

    @Delete
    void deletePlaylist(Playlist playlist);

    @Query("SELECT * FROM playlist")
    List<Playlist> loadAllPlaylist();

    @Query("SELECT * FROM playlist WHERE name = :name LIMIT 1")
    Playlist getPlaylistByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSongToPlaylist(SongList songList);

    @Query("DELETE FROM songlist WHERE songId = :songId")
    void removeSongFromPlaylist(int songId);
}
