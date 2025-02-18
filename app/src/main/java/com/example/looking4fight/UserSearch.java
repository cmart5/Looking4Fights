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

    // Implement Fuzzy Search
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<User> searchUsers(String query, LocalDate datePosted, int maxDistance) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(); // Return empty list if query is null or empty
        }

        String finalQuery = query.toLowerCase();

        return users.stream()
                .filter(user -> levenshteinDistance(user.getName().toLowerCase(), finalQuery) <= maxDistance)
                .filter(user -> datePosted == null || user.getDatePosted().isEqual(datePosted) || user.getDatePosted().isAfter(datePosted))
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        UserSearch userSearch = new UserSearch();

        String searchQuery = "Emly Dvis"; // Typo for testing
        LocalDate filterDate = LocalDate.of(2024, 1, 1); // Filter users posted on or after this date
        int maxDistance = 2;

        List<User> foundUsers = userSearch.searchUsers(searchQuery, filterDate, maxDistance);

        // Display results
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
