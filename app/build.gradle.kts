import java.util.Properties

fun String.toBuildConfigString(): String {
    return "\"" + trim().replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}

fun String.toBuildConfigBoolean(defaultValue: Boolean = false): String {
    return when (trim().lowercase()) {
        "true", "1", "yes", "si", "sí" -> "true"
        "false", "0", "no" -> "false"
        else -> defaultValue.toString()
    }
}

fun loadEnvironment(): Properties {
    val properties = Properties()
    val envLocalFile = rootProject.file(".env.local")
    val envFile = rootProject.file(".env")
    val selectedFile = when {
        envLocalFile.exists() -> envLocalFile
        envFile.exists() -> envFile
        else -> null
    }

    selectedFile?.inputStream()?.use { input ->
        properties.load(input)
    }

    return properties
}

val environmentProperties = loadEnvironment()

fun envValue(name: String, defaultValue: String = ""): String {
    return System.getenv(name)?.trim()
        ?: environmentProperties.getProperty(name)?.trim()
        ?: defaultValue
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
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

        manifestPlaceholders["androidMapsApiKey"] = envValue("ANDROID_MAPS_API_KEY")

        buildConfigField(
            "String",
            "EPAYCO_PUBLIC_KEY",
            envValue("EPAYCO_PUBLIC_KEY", "EPAYCO_PUBLIC_KEY_AQUI").toBuildConfigString()
        )
        buildConfigField(
            "Boolean",
            "EPAYCO_TEST_MODE",
            envValue("EPAYCO_TEST_MODE", "true").toBuildConfigBoolean(defaultValue = true)
        )
        buildConfigField(
            "String",
            "EPAYCO_MIN_AMOUNT",
            envValue("EPAYCO_MIN_AMOUNT", "1000").toBuildConfigString()
        )
        buildConfigField(
            "String",
            "EPAYCO_MAX_AMOUNT",
            envValue("EPAYCO_MAX_AMOUNT", "").toBuildConfigString()
        )
    }

    buildFeatures {
        buildConfig = true
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

    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps3d)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
