package com.example.looking4fight.ui.login.adapter;

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

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder> {
    private List<Post> userPosts;

    public ProfilePostAdapter(List<Post> userPosts) {
        this.userPosts = userPosts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = userPosts.get(position);
        holder.postText.setText(post.getText());

        // Load image if exists
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(holder.postImage.getContext()).load(post.getImageUrl()).into(holder.postImage);
        }
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postText;
        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postText = itemView.findViewById(R.id.post_caption);
            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}
