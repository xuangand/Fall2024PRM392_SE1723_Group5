package fu.se.spotifi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fu.se.spotifi.Entities.SearchHistory;
import fu.se.spotifi.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private final Context context;
    private List<SearchHistory> searchHistoryList;
    private final OnSearchItemClickListener onSearchItemClickListener;

    // Interface for handling item clicks
    public interface OnSearchItemClickListener {
        void onSearchItemClick(SearchHistory searchHistory);
    }

    // Constructor
    public SearchAdapter(Context context, List<SearchHistory> searchHistoryList, OnSearchItemClickListener onSearchItemClickListener) {
        this.context = context;
        this.searchHistoryList = searchHistoryList;
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(fu.se.spotifi.R.layout.item_search_history, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchHistory searchHistory = searchHistoryList.get(position);
        holder.bind(searchHistory, onSearchItemClickListener);
    }

    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }

    public void updateSearchHistory(List<SearchHistory> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        private final TextView searchQueryTextView;
        private final TextView searchDateTextView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            searchQueryTextView = itemView.findViewById(R.id.searchQueryTextView);
            searchDateTextView = itemView.findViewById(R.id.searchDateTextView);
        }

        public void bind(SearchHistory searchHistory, OnSearchItemClickListener listener) {
            searchQueryTextView.setText(searchHistory.getSearchQuery());
            searchDateTextView.setText(searchHistory.getSearchDate().toString()); // Format the date as needed

            itemView.setOnClickListener(v -> listener.onSearchItemClick(searchHistory));
        }
    }
}
