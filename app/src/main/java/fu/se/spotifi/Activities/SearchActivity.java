package fu.se.spotifi.Activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.SearchHistory;
import fu.se.spotifi.R;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listViewHistory;
    private List<String> searchHistory;
    private ArrayAdapter<String> historyAdapter;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        listViewHistory = findViewById(R.id.listViewHistory);

        searchHistory = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistory);
        listViewHistory.setAdapter(historyAdapter);

        loadSearchHistory();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private void loadSearchHistory() {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            List<SearchHistory> historyList = db.searchHistoryDAO().loadAllSearchHistory();

            runOnUiThread(() -> {
                for (SearchHistory history : historyList) {
                    searchHistory.add(history.getSearchQuery());
                }
                historyAdapter.notifyDataSetChanged();
            });
        });
    }

    private void performSearch(String query) {
        // Save the search query
        saveSearchQuery(query);

        // Perform your search logic here (e.g., search songs in the database)

        // Update UI with results
        searchHistory.add(0, query);
        historyAdapter.notifyDataSetChanged();
    }

    private void saveSearchQuery(String query) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setSearchQuery(query);
            searchHistory.setSearchDate(new Date());
            db.searchHistoryDAO().insertSearchHistory(searchHistory);
        });
    }
}

