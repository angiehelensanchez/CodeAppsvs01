// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.1")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

allprojects {
    // No repositories block here
}