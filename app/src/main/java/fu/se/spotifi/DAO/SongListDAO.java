package fu.se.spotifi.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.Entities.SongList;

@Dao
public interface SongListDAO {

    @Insert
    void addSongList(SongList songList);

    @Delete
    void deleteSongList(SongList songList);

    @Query("SELECT * FROM songlist")
    List<SongList> loadAllSongList();

    @Query("SELECT songs.* FROM songs INNER JOIN songlist ON songs.id = songlist.songId WHERE songlist.playlistId = :playlistId")
    List<Song> loadSongsByPlaylistId(int playlistId);
}
