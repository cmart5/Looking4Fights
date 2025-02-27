package com.example.looking4fight.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.looking4fight.R;
import com.example.looking4fight.data.model.Event;
import com.example.looking4fight.data.model.EventAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment
{
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private GoogleMap mMap;

    public EventsFragment()
    {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        if (recyclerViewEvents == null)
        {
            Log.e("EventsFragment", "RecyclerView is null");
        }
        else
        {
            recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        // Initialize adapter
        eventAdapter = new EventAdapter(new ArrayList<>());
        recyclerViewEvents.setAdapter(eventAdapter);

//        // Load the map fragment
//        SupportMapFragment mapFragment = (SupportMapFragment)
//                getChildFragmentManager().findFragmentById(R.id.mapFragment);
//
//        if (mapFragment != null)
//        {
//            mapFragment.getMapAsync(this);
//        }

//         Fetch events from Firestore
        fetchEvents();
    }

//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap)
//    {
//        mMap = googleMap;
//
//        // Set default location (example: New York City)
//        LatLng defaultLocation = new LatLng(40.7128, -74.0060);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
//
//        // Add a sample marker
//        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Event Location"));
//    }

    private void fetchEvents()
    {
        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) ->
                {
                    if (error != null)
                    {
                        Log.e("EventsFragment", "Error fetching events", error);
                        return;
                    }

                    List<Event> eventList = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments())
                    {
                        Event event = doc.toObject(Event.class);
                        eventList.add(event);
                    }

                    eventAdapter.setEvents(eventList);
                });
    }
}
