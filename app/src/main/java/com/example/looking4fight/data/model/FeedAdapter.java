package com.example.looking4fight.data.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.looking4fight.R;
import com.example.looking4fight.data.model.Post;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<Post> postList;
    private Context context;

    public FeedAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.username.setText(post.getUsername());
        holder.caption.setText(post.getCaption());
        holder.likeCount.setText(post.getLikeCount() + " likes");

        Glide.with(context).load(post.getImageUrl()).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView username, caption, likeCount;
        ImageView postImage;

        public FeedViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.post_username);
            caption = itemView.findViewById(R.id.post_caption);
            likeCount = itemView.findViewById(R.id.post_likes);
            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}
