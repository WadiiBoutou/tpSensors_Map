plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.drawer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.drawer"
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
    }
    buildFeatures {
        viewBinding = true
    }
}


  dependencies {

      implementation("com.squareup.retrofit2:retrofit:2.9.0")
      implementation("com.squareup.retrofit2:converter-gson:2.9.0")
      implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
      implementation("com.google.android.gms:play-services-location:21.0.1")
      implementation("com.google.android.gms:play-services-maps:18.1.0")
      implementation(libs.appcompat)
      implementation(libs.material)
      implementation(libs.constraintlayout)
      implementation(libs.lifecycle.livedata.ktx)
      implementation(libs.lifecycle.viewmodel.ktx)
      implementation(libs.navigation.fragment)
      implementation(libs.navigation.ui)
      implementation(libs.legacy.support.v4)
      implementation(libs.recyclerview)
      testImplementation(libs.junit)
      androidTestImplementation(libs.ext.junit)
      androidTestImplementation(libs.espresso.core)

  }
