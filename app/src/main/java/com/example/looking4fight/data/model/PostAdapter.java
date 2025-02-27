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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImage;
        private TextView postTitle;
        private final TextView postUsername;
        private ImageView likeButton;
        private TextView likesCountText;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postTitle = itemView.findViewById(R.id.post_title);
            postUsername = itemView.findViewById(R.id.post_username);
            likeButton = itemView.findViewById(R.id.like_button);
            likesCountText = itemView.findViewById(R.id.like_count);
        }

        private void toggleLike(final Post post, final String currentUserID, final int position) {
            final DocumentReference postRef = db.collection("posts").document(post.getPostId());
            final DocumentReference likeRef = postRef.collection("likes").document(currentUserID);

            likeRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // 🔹 Already liked -> Unlike it
                    likeRef.delete().addOnSuccessListener(aVoid -> {

                        // 🔹 Decrement like count
                        db.runTransaction(transaction -> {
                            DocumentReference postRefTx = db.collection("posts").document(post.getPostId());
                            DocumentSnapshot snapshot = transaction.get(postRefTx);

                            long currentLikes = snapshot.getLong("likesCount") != null ? snapshot.getLong("likesCount") : 0;
                            transaction.update(postRefTx, "likesCount", Math.max(currentLikes - 1, 0));

                            return null;
                        }).addOnSuccessListener(aVoid2 -> {
                            post.setLikeCount(Math.max(post.getLikesCount() - 1, 0));  // Update post object
                            likeButton.setImageResource(R.drawable.ic_heart_outline);
                            likesCountText.setText(String.valueOf(post.getLikesCount()));
                        });

                    });
                } else {
                    // Not liked -> Like it
                    Map<String, Object> likeMap = new HashMap<>();
                    likeMap.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                    likeRef.set(likeMap).addOnSuccessListener(aVoid -> {

                        // Increment the like count safely
                        db.runTransaction(transaction -> {
                            DocumentReference postRefTx = db.collection("posts").document(post.getPostId());
                            DocumentSnapshot snapshot = transaction.get(postRefTx);

                            long currentLikes = snapshot.getLong("likesCount") != null ? snapshot.getLong("likesCount") : 0;
                            transaction.update(postRefTx, "likesCount", currentLikes + 1);

                            return null;
                        }).addOnSuccessListener(aVoid2 -> {
                            post.setLikeCount(post.getLikesCount() + 1);  // Update post object
                            likeButton.setImageResource(R.drawable.ic_filled_heart);
                            likesCountText.setText(String.valueOf(post.getLikesCount()));
                        });

                    });
                }
            });
        }

private void checkIfLiked(final PostViewHolder holder, final Post post, final String currentUserID) {

            DocumentReference likeRef = db.collection("posts")
                    .document(post.getPostId())
                    .collection("likes")
                    .document(currentUserID);

            likeRef.addSnapshotListener((snapshot, e)-> {
                if (snapshot != null && snapshot.exists()) {

                    //Already liked
                    holder.likeButton.setImageResource(R.drawable.ic_filled_heart);
                } else {
                    //Not liked
                    holder.likeButton.setImageResource(R.drawable.ic_heart_outline);
                }
            });
}

        public void bind(Post post) {
            postTitle.setText(post.getTitle());

            // Set default text while loading username
            postUsername.setText("Loading...");

            // Fetch username from Firestore based on userId
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(post.getUserId());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("name");
                    postUsername.setText(username != null ? username : "Unknown User");
                } else {
                    postUsername.setText("Unknown User");
                }
            }).addOnFailureListener(e -> postUsername.setText("Error"));

            // Load post image
            Glide.with(itemView.getContext()).load(post.getMediaUrl()).into(postImage);

            //Show like count
            likesCountText.setText(String.valueOf(post.getLikesCount()));
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
        Post post = postList.get(position);
        holder.bind(post);

        //Get current user ID (Make sure user is authenticated)
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //check if post is already liked by current user
        holder.checkIfLiked(holder, post, currentUserID);

        //Set the like button's click listener to toggle the like state
        holder.likeButton.setOnClickListener(v ->{
            holder.toggleLike(post, currentUserID, position);

            //Re-check like state to update UI
            holder.checkIfLiked(holder, post, currentUserID);
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

}


