plugins {
    id("com.android.application")
}

android {
    namespace = "com.vishnumoyal14.jarvis"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vishnumoyal14.jarvis"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
