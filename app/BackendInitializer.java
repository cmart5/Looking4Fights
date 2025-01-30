import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class BackendInitializer {

    // My Firebase initialization method
    public static void initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("path/to/your/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://your-database-name.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }

    // This is how I create a new user
    public static UserRecord createUser(String email, String password) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        return FirebaseAuth.getInstance().createUser(request);
    }

    // This method gives me access to Firestore
    public static Firestore getFirestore() {
        return FirestoreOptions.getDefaultInstance().getService();
    }

    // I save video posts with this method
    public static void saveVideoPost(String userId, String videoUrl, String title, String description) {
        Firestore db = getFirestore();

        db.collection("video_posts").add(new VideoPost(userId, videoUrl, title, description))
                .addOnSuccessListener(documentReference ->
                        System.out.println("Video post saved with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        System.err.println("Error adding video post: " + e.getMessage()));
    }

    // This saves user info to Firestore
    public static void saveUser(String userId, String name, String email, String role, String weightClass, String bio, String profileImage) {
        Firestore db = getFirestore();

        db.collection("users").add(new User(userId, name, email, role, weightClass, bio, profileImage))
                .addOnSuccessListener(documentReference ->
                        System.out.println("User saved with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        System.err.println("Error adding user: " + e.getMessage()));
    }

    // Saving fight event metadata
    public static void saveFight(String fightId, String title, String location, String date, String[] fighters, String promoterId) {
        Firestore db = getFirestore();

        db.collection("fights").add(new Fight(fightId, title, location, date, fighters, promoterId))
                .addOnSuccessListener(documentReference ->
                        System.out.println("Fight saved with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        System.err.println("Error adding fight: " + e.getMessage()));
    }

    public static void main(String[] args) {
        try {
            // Firebase setup
            initializeFirebase();
            System.out.println("Firebase Initialized Successfully");

            // Example of creating a user
            UserRecord newUser = createUser("example@example.com", "password123");
            System.out.println("User created successfully: " + newUser.getUid());

            // Example Firestore connection
            Firestore db = getFirestore();
            System.out.println("Firestore Initialized Successfully");

            // Adding some example data
            saveVideoPost("userId123", "https://example.com/video.mp4", "My First Fight", "An amazing fight video!");
            saveUser("userId123", "John Doe", "johndoe@example.com", "fighter", "Lightweight", "Ready to fight anyone, anytime!", "https://example.com/johndoe.jpg");
            saveFight("fightId123", "Main Event", "Las Vegas", "2025-03-01", new String[]{"userId123", "userId456"}, "promoterId789");

        } catch (IOException e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Structure for video posts
    static class VideoPost {
        private String userId;
        private String videoUrl;
        private String title;
        private String description;

        public VideoPost(String userId, String videoUrl, String title, String description) {
            this.userId = userId;
            this.videoUrl = videoUrl;
            this.title = title;
            this.description = description;
        }
    }

    // Structure for users
    static class User {
        private String userId;
        private String name;
        private String email;
        private String role;
        private String weightClass;
        private String bio;
        private String profileImage;

        public User(String userId, String name, String email, String role, String weightClass, String bio, String profileImage) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.weightClass = weightClass;
            this.bio = bio;
            this.profileImage = profileImage;
        }
    }

    // Structure for fight events
    static class Fight {
        private String fightId;
        private String title;
        private String location;
        private String date;
        private String[] fighters;
        private String promoterId;

        public Fight(String fightId, String title, String location, String date, String[] fighters, String promoterId) {
            this.fightId = fightId;
            this.title = title;
            this.location = location;
            this.date = date;
            this.fighters = fighters;
            this.promoterId = promoterId;
        }
    }
}
