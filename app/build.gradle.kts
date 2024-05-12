@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id ("com.google.dagger.hilt.android")
    id ("com.google.devtools.ksp")
}

android {
    namespace = "com.taein.travelmap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.taein.travelmap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    viewBinding {
        enable = true
    }
}

dependencies {
    implementation("io.github.fornewid:naver-map-compose:1.6.2")
    // (Optional) 위치 추적하기
    // 다른 버전의 play-services-location 과 함께 사용하려면 선언해야 합니다.
    // 선언하지 않았을 때, 기본으로 포함되는 play-services-location 버전은 16.0.0 입니다.
    implementation("io.github.fornewid:naver-map-location:21.0.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation(libs.google.material.icons.compose)
    implementation(libs.androidx.ui.viewbinding)
    implementation(libs.hilt.android)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.material3.android)
    implementation(libs.material)
    ksp(libs.hilt.compiler)
    implementation(libs.map.sdk)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.exifinterface)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
}