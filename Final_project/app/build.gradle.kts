plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.englishapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.englishapp"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-analytics:21.4.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.android.gms:play-services-tasks:18.0.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.3.0-alpha02")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.opencsv:opencsv:4.6")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.sun.mail:android-mail:1.6.5")
    implementation("com.sun.mail:android-activation:1.6.5")
    implementation("com.google.android.material:material:1.2.0-alpha03")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    //noinspection GradleCompatible





    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.wajahatkarim:EasyFlipView:3.0.3")
}