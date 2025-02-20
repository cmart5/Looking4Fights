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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Null-check before setting values
        if (post.getTitle() != null) {
            holder.postTitle.setText(post.getTitle());
        } else {
            holder.postTitle.setText("No Title");
        }

        if (post.getDescription() != null) {
            holder.postCaption.setText(post.getDescription());
        } else {
            holder.postCaption.setText("No caption available.");
        }

        // Set default username text
        holder.postUsername.setText("Loading...");

        if (post.getUserId() != null) {
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(post.getUserId());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.getString("name") != null) {
                    holder.postUsername.setText(documentSnapshot.getString("name"));
                } else {
                    holder.postUsername.setText("Unknown User");
                }
            }).addOnFailureListener(e -> holder.postUsername.setText("Error"));
        } else {
            holder.postUsername.setText("Unknown User");
        }

        // Load image if exists
        if (post.getMediaUrl() != null && !post.getMediaUrl().isEmpty()) {
            Glide.with(holder.postImage.getContext()).load(post.getMediaUrl()).into(holder.postImage);
        } else {
            holder.postImage.setImageResource(R.drawable.default_profile); // Fallback image
        }
    }

    public void setPosts(List<Post> newPosts) {
        this.userPosts.clear();
        this.userPosts.addAll(newPosts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postCaption, postUsername;
        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.post_title);
            postCaption = itemView.findViewById(R.id.post_caption);
            postUsername = itemView.findViewById(R.id.post_username);
            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}
