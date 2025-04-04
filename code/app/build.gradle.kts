plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.canada_geese"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.canada_geese"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true

    }
    testOptions {
        unitTests {
            all {
                it.jvmArgs("-Dnet.bytebuddy.experimental=true")
            }

            isReturnDefaultValues = true
        }
    }
    packaging {
        resources {
            excludes += "mockito-extensions/org.mockito.plugins.MockMaker"
        }
    }
}
configurations.all {
    exclude(group = "com.google.protobuf", module = "protobuf-lite")
    exclude(group = "com.google.android", module = "annotations")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    // Firebase
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // AndroidX & UI
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.core:core-ktx:1.12.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.espresso.core)
    implementation(libs.appcompat)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.test.espresso:espresso-contrib:3.5.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // Room (for local database)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Gson (for serializing imagePaths in Room DB)
    implementation("com.google.code.gson:gson:2.10.1")

    // WorkManager (for syncing offline moods in background)
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("com.google.guava:guava:31.1-android")


    // Unit Tests (JVM)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("androidx.test:rules:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.fragment:fragment-testing:1.6.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")

    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.test.ext:truth:1.5.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    testImplementation("org.mockito:mockito-inline:4.5.1")

    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.0")
    androidTestImplementation("org.mockito:mockito-android:5.2.0")
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.1")

    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.android", module = "annotations")
    }
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

}
