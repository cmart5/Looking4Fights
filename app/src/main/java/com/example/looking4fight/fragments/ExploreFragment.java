package com.example.looking4fight.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ExploreFragment extends Fragment
{
    private SearchView searchView;
    private RecyclerView recyclerView;
    private PostAdapter exploreAdapter;

    List<Post> postList = new ArrayList<>();

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

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterList(query);
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2-column grid

        // Initialize the adapter with an empty list and set it on the RecyclerView
        exploreAdapter = new PostAdapter(new ArrayList<>());
        recyclerView.setAdapter(exploreAdapter);

        fetchPosts();  // Call fetchPosts() to load posts
    }

    private void fetchPosts()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .orderBy("timestampMillis", Query.Direction.DESCENDING)
                .addSnapshotListener((QuerySnapshot value, FirebaseFirestoreException error) ->
                {
                    if (error != null)
                    {
                        Log.e("ExploreFragment", "Failed to fetch posts", error);
                        return;
                    }

                    for (DocumentSnapshot doc : value.getDocuments())
                    {
                        Post post = doc.toObject(Post.class);
                        postList.add(post);
                    }

                    if (exploreAdapter != null)
                    {
                        exploreAdapter.setPosts(postList);
                    }
                    else
                    {
                        Log.e("ExploreFragment", "Adapter is null, cannot update posts");
                    }
                });
    }

    private void filterList(String searchQuery) {
        List<Post> filteredList = applyQuery(searchQuery, 2);
        exploreAdapter.setPosts(filteredList);
    }

    private List<Post> applyQuery(String query, int maxDistance) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(postList); // Return all posts if query is empty
        }

        String finalQuery = query.toLowerCase();
        return postList.stream()
                .filter(post -> post.getTitle().toLowerCase().contains(finalQuery) ||
                        levenshteinDistance(post.getTitle().toLowerCase(), finalQuery) <= maxDistance)
                .sorted(Comparator.comparingInt((Post post) -> post.getTitle().length()).reversed()) // Prioritize longer titles
                .collect(Collectors.toList());
    }

    // Levenshtein Distance Algorithm
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(
                                    dp[i - 1][j] + 1,  // Deletion
                                    dp[i][j - 1] + 1), // Insertion
                            dp[i - 1][j - 1] + cost    // Substitution
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
