plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId  = "com.mint.minttracker"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.incremental" to "true",
                    "room.schemaLocation" to "schemas/",
                )
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding =  true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("com.google.android.material:material:1.5.0-alpha04")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation ("junit:junit:4.+")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    implementation ("androidx.fragment:fragment-ktx:1.3.2")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation ("com.google.code.gson:gson:2.8.7")

    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.1")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.1.0")

    implementation ("com.google.maps.android:maps-compose:2.9.0")

    implementation ("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")

    implementation ("androidx.room:room-runtime:2.4.1")
    implementation ("androidx.room:room-rxjava2:2.4.1")
    kapt ("androidx.room:room-compiler:2.4.1")

    kapt ("com.google.dagger:dagger-compiler:2.43.2")
    implementation ("com.google.dagger:dagger:2.43.2")

    implementation (platform("com.google.firebase:firebase-bom:31.2.0"))
    implementation ("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    implementation ("androidx.compose.compiler:compiler:1.3.0")
    implementation ("androidx.compose.foundation:foundation:1.3.0")
    implementation ("androidx.compose.material:material:1.3.0")
    implementation ("androidx.compose.runtime:runtime:1.3.0")
    implementation ("androidx.compose.ui:ui:1.3.0")
    implementation ("androidx.compose.ui:ui-tooling:1.3.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
}
