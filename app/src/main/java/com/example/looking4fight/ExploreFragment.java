package com.example.looking4fight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter exploreAdapter;
    private List<String> testItems;
    private List<String> initialTestItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sample test items
        initialTestItems = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            initialTestItems.add("Test Item " + i);
        }
        // Additional testing data
        initialTestItems.add("Runtime Test Item 1");
        initialTestItems.add("Cheese Pizza");
        initialTestItems.add("Jon Doe");
        initialTestItems.add("New user post");

        testItems = initialTestItems;

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2-column grid

        // Set adapter
        exploreAdapter = new RecyclerView.Adapter()
        {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((android.widget.TextView) holder.itemView).setText(testItems.get(position));
            }

            @Override
            public int getItemCount()
            {
                return testItems.size();
            }
        };
        recyclerView.setAdapter(exploreAdapter);
    }
    private void setFilteredList(List<String> filteredList) {
        testItems = filteredList;
        exploreAdapter.notifyDataSetChanged();
    }

    private void filterList(String text) {
        List<String> filteredList = new ArrayList<>();
        testItems = initialTestItems;
        for(String item : testItems) {
            if(item.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No items found", Toast.LENGTH_SHORT).show();
        }
        setFilteredList(filteredList);
    }
}
