package com.example.looking4fight.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.looking4fight.R;
import com.example.looking4fight.data.model.Post;
import com.example.looking4fight.data.model.PostAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment
{

    private RecyclerView recyclerView;
    private PostAdapter exploreAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2-column grid

        // Initialize the adapter with an empty list and set it on the RecyclerView
        exploreAdapter = new PostAdapter(new ArrayList<>());
        recyclerView.setAdapter(exploreAdapter);

        ImageButton buttonOpenCreatePost = view.findViewById(R.id.buttonOpenCreatePost);
        buttonOpenCreatePost.setOnClickListener(v ->
        {
            Fragment createPostFragment = new CreatePostFragment();

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

            transaction.setReorderingAllowed(true); // Improves performance
            transaction.add(android.R.id.content, createPostFragment); // Adds it on top
            transaction.addToBackStack(null); // Allows back navigation
            transaction.commit();
        });
    }

//    private void fetchPosts()
//    {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("posts")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .addSnapshotListener((value, error) ->
//                {
//                    if (error != null)
//                    {
//                        Log.e("Firestore", "Failed to fetch posts", error);
//                        return;
//                    }
//
//                    List<Post> postList = new ArrayList<>();
//                    for (DocumentSnapshot doc : value.getDocuments())
//                    {
//                        Post post = doc.toObject(Post.class);
//                        postList.add(post);
//                    }
//
//                    if(exploreAdapter != null)
//                    {
//                        exploreAdapter.setPosts(postList);
//                    }
//                    else
//                    {
//                        Log.e("ExploreFragment", "Failed to fetch posts");
//                    }
//                });
//
//        fetchPosts();
//    }
}
