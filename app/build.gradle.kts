plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "fu.se.spotifi"
    compileSdk = 34

    defaultConfig {
        applicationId = "fu.se.spotifi"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)
    implementation(libs.room.common)
    implementation(libs.media3.common)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.ffmpegmediametadataretriever.core)
    implementation (libs.ffmpegmediametadataretriever.native)
    implementation (libs.firebase.storage)
    implementation (libs.glide)
    implementation(libs.media3.exoplayer)
}
