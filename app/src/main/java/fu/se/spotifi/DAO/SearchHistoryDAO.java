package fu.se.spotifi.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fu.se.spotifi.Entities.SearchHistory;

@Dao
public interface SearchHistoryDAO {

    @Insert
    void insertSearchHistory(SearchHistory searchHistory);

    @Query("SELECT * FROM search_history ORDER BY searchDate DESC")
    List<SearchHistory> loadAllSearchHistory();

    @Query("DELETE FROM search_history")
    void clearSearchHistory();
}
