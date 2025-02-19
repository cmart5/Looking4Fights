package com.example.looking4fight.data.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.looking4fight.R;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImage;
        private TextView postTitle;
        private TextView postUserId;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postTitle = itemView.findViewById(R.id.post_title);
            postUserId = itemView.findViewById(R.id.post_user_id);
        }

        public void bind(Post post) {
            postTitle.setText(post.getTitle());
            postUserId.setText(post.getUserId());
            Glide.with(itemView.getContext()).load(post.getMediaUrl()).into(postImage);
        }
    }
    public void setPosts(List<Post> newPosts) {
        this.postList.clear();  // Clear the current dataset
        this.postList.addAll(newPosts); // Add new posts
        notifyDataSetChanged(); // Notify RecyclerView to refresh UI
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

}


