
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-kapt")
}

//val admobAppId: String = project.findProperty("ADMOB_APP_ID") as? String ?: ""
//val interstitialAdId: String = project.findProperty("INTERSTITIAL_AD_ID") as? String ?: ""

//These are test IDs only if app fails
val admobAppId: String = project.findProperty("ADMOB_APP_ID") as? String
    ?:"ca-app-pub-3940256099942544~3347511713"

val interstitialAdId: String = project.findProperty("INTERSTITIAL_AD_ID") as? String
    ?:"ca-app-pub-3940256099942544/1033173712"

val feedBackAds: String = project.findProperty("FEEDBACK_EMAIL") as? String ?:"your@email.com"

//test banner ad id
val bannerAdsID: String = project.findProperty("BANNER_ADS_ID")as? String ?:"ca-app-pub-3940256099942544/6300978111"



android {
    namespace = "com.nutrino.audiocutter"
    compileSdk = 36

    defaultConfig {
        manifestPlaceholders["ADMOB_APP_ID"] = admobAppId
        buildConfigField("String", "ADMOB_APP_ID", "\"$admobAppId\"")
        buildConfigField("String", "INTERSTITIAL_AD_ID", "\"$interstitialAdId\"")
        buildConfigField("String","FEEDBACK_EMAIL","\"$feedBackAds\"")
        buildConfigField("String","BANNER_ADS_ID","\"$bannerAdsID\"")
        applicationId = "com.nutrino.audiocutter"
        minSdk = 24
        targetSdk = 36
        versionCode = 14
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }

    buildTypes {
        buildFeatures.buildConfig = true
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    val room_version = "2.7.0"
    //  implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation(libs.androidx.navigation)

    // Media3 - Use consistent version 1.5.0 for better compatibility with API 24+
    implementation ("androidx.media3:media3-transformer:1.5.0")
    implementation ("androidx.media3:media3-exoplayer:1.5.0")
    implementation ("androidx.media3:media3-ui:1.5.0")
    implementation ("androidx.media3:media3-common:1.5.0")
    implementation ("androidx.media3:media3-session:1.5.0")
    implementation("androidx.media3:media3-effect:1.5.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.core:core-splashscreen:1.0.0")
    //ads
    implementation("com.google.android.gms:play-services-ads:24.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
}