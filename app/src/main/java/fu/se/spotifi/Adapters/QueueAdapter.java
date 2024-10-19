package fu.se.spotifi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.R;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private Context context;
    private List<Queue> queueList;

    public QueueAdapter(Context context, List<Queue> queueList) {
        this.context = context;
        this.queueList = queueList;
    }

    public void setQueueList(List<Queue> queueList) {
        this.queueList = queueList;
        notifyDataSetChanged();  // Notify the adapter to refresh the views
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_queue_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Queue queueItem = queueList.get(position);
        holder.songTitle.setText(queueItem.getSongTitle());
        holder.songArtist.setText(queueItem.getSongArtist());
        Glide.with(context).load(queueItem.getSongThumbnail()).into(holder.songThumbnail);
        holder.queuePosition.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return queueList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist,duration,queuePosition,songUrl;
        ImageView songThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
            //duration = itemView.findViewById(R.id.songDuration);
            songThumbnail = itemView.findViewById(R.id.songThumbnail);
            queuePosition = itemView.findViewById(R.id.queuePosition);
        }
    }
}


