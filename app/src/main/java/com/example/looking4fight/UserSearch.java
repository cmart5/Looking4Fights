package com.example.looking4fight;
import java.util.ArrayList;
import java.util.List;

public class UserSearch {
    private List<User> users;

    public UserSearch() {
        // Sample user data
        users = new ArrayList<>();
        users.add(new User(1, "John Doe"));
        users.add(new User(2, "Jane Smith"));
        users.add(new User(3, "Jack Johnson"));
        users.add(new User(4, "Emily Davis"));
    }

    // Search function to find users by name (case-insensitive, partial match)
    public List<User> searchUsers(String query) {
        List<User> results = new ArrayList<>();
        query = query.toLowerCase();

        for (User user : users) {
            if (user.getName().toLowerCase().contains(query)) {
                results.add(user);
            }
        }
        return results;
    }

    public static void main(String[] args) {
        UserSearch userSearch = new UserSearch();

        // Example search
        String searchQuery = "Jo";
        List<User> foundUsers = userSearch.searchUsers(searchQuery);

        // Display results
        if (foundUsers.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("Search Results:");
            for (User user : foundUsers) {
                System.out.println("ID: " + user.getId() + ", Name: " + user.getName());
            }
        }
    }
}