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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>
{
    private List<Post> posts;

    public PostAdapter(List<Post> posts)
    {
        this.posts = posts;
    }

    public void setPosts(List<Post> posts)
    {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position)
    {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount()
    {
        return posts != null ? posts.size() : 0;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView postImage;
        private TextView postCaption, postUsername;

        public PostViewHolder(@NonNull View itemView)
        {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postCaption = itemView.findViewById(R.id.post_caption);
            postUsername = itemView.findViewById(R.id.post_username);
        }

        public void bind(Post post)
        {
            postCaption.setText(post.getCaption());
            postUsername.setText(post.getUsername());
            Glide.with(itemView.getContext()).load(post.getImageUrl()).into(postImage);
        }
    }
}
