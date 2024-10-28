package fu.se.spotifi.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private final Context context;
    private List<Queue> queueList;
    private final Utils utils = new Utils();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        executorService.execute(() -> {
            Queue queueItem = queueList.get(position);
            SpotifiDatabase db = SpotifiDatabase.getInstance(context);
            Song song = db.songDAO().getSongById(queueItem.getSongId());

            Activity mainActivity = (Activity) context;
            mainActivity.runOnUiThread(() -> {
                holder.songTitle.setText(song.getTitle());
                holder.songArtist.setText(song.getArtist());
                holder.queuePosition.setText(String.valueOf(position + 1));
                holder.duration.setText(utils.milisecondsToString(song.getDuration()));
                Glide.with(context).load(song.getThumbnail()).into(holder.songThumbnail);
            });
        });
    }

    @Override
    public int getItemCount() {
        return queueList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist, duration, queuePosition;
        ImageView songThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
            duration = itemView.findViewById(R.id.songDuration);
            songThumbnail = itemView.findViewById(R.id.songThumbnail);
            queuePosition = itemView.findViewById(R.id.queuePosition);
        }
    }
}


