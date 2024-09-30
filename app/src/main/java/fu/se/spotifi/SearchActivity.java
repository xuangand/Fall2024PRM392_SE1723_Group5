package fu.se.spotifi;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listViewHistory;
    private ListView listViewResults;
    private List<String> searchHistory;
    private List<String> searchResults;
    private ArrayAdapter<String> historyAdapter;
    private ArrayAdapter<String> resultsAdapter;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        listViewHistory = findViewById(R.id.listViewHistory);
        listViewResults = findViewById(R.id.listViewResults);
        backButton = findViewById(R.id.backButton);

        searchHistory = new ArrayList<>();
        searchResults = new ArrayList<>();

        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistory);
        resultsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);

        listViewHistory.setAdapter(historyAdapter);
        listViewResults.setAdapter(resultsAdapter);

        // Customize list item text color
        historyAdapter.setNotifyOnChange(true);
        resultsAdapter.setNotifyOnChange(true);

        // Add some dummy search history
        searchHistory.add("Recent search 1");
        searchHistory.add("Recent search 2");
        searchHistory.add("Recent search 3");
        historyAdapter.notifyDataSetChanged();

        // Set up the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showSearchHistory();
                } else {
                    showSearchResults();
                }
                return true;
            }
        });

        // Customize SearchView text color
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.LTGRAY);

        // Set up the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and return to the previous one
            }
        });
    }

    private void performSearch(String query) {
        // Clear previous results
        searchResults.clear();

        // Add the search query to history
        searchHistory.add(0, query);
        historyAdapter.notifyDataSetChanged();

        // Perform the search (dummy results for demonstration)
        searchResults.add("Song 1 - " + query);
        searchResults.add("Artist - " + query);
        searchResults.add("Album - " + query);

        resultsAdapter.notifyDataSetChanged();
        showSearchResults();
    }

    private void showSearchHistory() {
        listViewHistory.setVisibility(ListView.VISIBLE);
        listViewResults.setVisibility(ListView.GONE);
    }

    private void showSearchResults() {
        listViewHistory.setVisibility(ListView.GONE);
        listViewResults.setVisibility(ListView.VISIBLE);
    }
}

