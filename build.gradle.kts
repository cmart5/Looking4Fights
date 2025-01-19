// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.0-alpha05" apply false

    // Adds the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

    id("com.android.library") version "8.8.0-alpha05" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
}