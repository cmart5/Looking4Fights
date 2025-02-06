package com.example.looking4fight;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSearch {
    private List<User> users;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UserSearch() {
        // Sample user data
        users = new ArrayList<>();
        users.add(new User(1, "John Doe", LocalDate.of(2024, 2, 1)));
        users.add(new User(2, "Jane Smith", LocalDate.of(2024, 1, 15)));
        users.add(new User(3, "Jack Johnson", LocalDate.of(2023, 12, 20)));
        users.add(new User(4, "Emily Davis", LocalDate.of(2024, 2, 5)));
    }

    // Search function to find users by name with optional date filter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<User> searchUsers(String query, LocalDate datePosted) {
        query = query.toLowerCase();
        String finalQuery = query;
        return users.stream()
                .filter(user -> user.getName().toLowerCase().contains(finalQuery))
                .filter(user -> datePosted == null || user.getDatePosted().isEqual(datePosted) || user.getDatePosted().isAfter(datePosted))
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        UserSearch userSearch = new UserSearch();

        // Example search with a date filter
        String searchQuery = "Em";
        LocalDate filterDate = LocalDate.of(2024, 1, 1); // Filter users posted on or after this date
        List<User> foundUsers = userSearch.searchUsers(searchQuery, filterDate);

        // Example of displaying results
        if (foundUsers.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("Search Results:");
            for (User user : foundUsers) {
                System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Date Posted: " + user.getDatePosted());
            }
        }
    }
}
