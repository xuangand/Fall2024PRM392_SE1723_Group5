package fu.se.spotifi.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fu.se.spotifi.Entities.Song;

@Dao
public interface SongDAO {
    @Insert
    void addSong(Song song);

    @Update
    void updateSong(Song song);

    @Delete
    void deleteSong(Song song);

    @Query("SELECT * FROM songs")
    List<Song> loadAllSongs();
}
