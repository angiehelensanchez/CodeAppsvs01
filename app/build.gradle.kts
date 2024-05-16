import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.codeappsvs01"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.codeappsvs01"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation ("com.google.android.gms:play-services-location:21.0.1")


    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // Login Google
    implementation("com.google.android.gms:play-services-auth:20.1.0")

    // Si estás usando Firebase Auth
    implementation("com.google.firebase:firebase-auth")

    // Dependencias de Room
    implementation(libs.room.runtime)
    implementation(libs.recyclerview)
    implementation(libs.room.rxjava)


    // RxJava y RxAndroid
    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    // Dependencia para Android-GIF-Drawable
    implementation(libs.androidgifdrawable)
    implementation(libs.glide)


}
