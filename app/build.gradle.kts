plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    // id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.compensar.tienda"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.compensar.tienda"
        minSdk = 29
        targetSdk = 36
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
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)

        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        ksp(libs.androidx.room.compiler)
        implementation(libs.gson)
        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.gson)
        implementation(libs.play.services.location)

        // Firebase Firestore
        implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
        implementation("com.google.firebase:firebase-firestore")
        // implementation("com.google.firebase:firebase-analytics")
        // Firebase Storage
        implementation("com.google.firebase:firebase-storage")
        implementation("com.github.bumptech.glide:glide:4.16.0")

        // Ubicación en tiempo real
        implementation("com.google.android.gms:play-services-location:21.3.0")
        // Google Maps
        implementation("com.google.android.gms:play-services-maps:19.0.0")
        // Huella Biometrica
        implementation("androidx.biometric:biometric:1.2.0-alpha05")

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }

    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.play.services.maps3d)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}