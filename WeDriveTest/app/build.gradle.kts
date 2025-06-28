plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    alias(libs.plugins.navigation.safe.args)
}

// 클라이언트에서 요청할 Rest API 서버 주소
val apiUrl = "https://codetest.wedrive.kr:7880/"

android {
    namespace  = "com.wedrive.test"
    compileSdk = libs.versions.compileSdk.get().toInt()

    val versionInfo = setVersion()
    val versionCd   = versionInfo[0] as Int
    val versionNm   = versionInfo[1] as String

    defaultConfig {
        applicationId             = "com.wedrive.test"
        minSdk                    = libs.versions.minSdk.get().toInt()
        targetSdk                 = libs.versions.targetSdk.get().toInt()
        versionCode               = versionCd
        versionName               = versionNm
        multiDexEnabled           = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 출력 파일명 설정
        setProperty("archivesBaseName", "${rootProject.name}-$versionName")
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            resValue("string", "api_host", apiUrl)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}

private fun setVersion(): List<Any> {
    val major   = libs.versions.major.get().toInt()
    val minor   = libs.versions.minor.get().toInt()
    val patch   = libs.versions.patch.get().toInt()
    val develop = libs.versions.develop.get().toInt()

    var versionCode = major * 10000 + minor * 100 + patch
    var versionName = "$major.$minor.$patch"

    if (develop > 0) {
        versionCode = versionCode * 100 + develop
        versionName += ".d$develop"
    }

    return listOf(versionCode, versionName)
}

dependencies {
    implementation(project(path = ":base"))

    // Android Jetpack
    implementation(libs.bundles.android.jetpack)

    // Logging
    implementation(libs.timber)
    implementation(libs.okHttpLogging)

    // Moshi
    implementation(libs.bundles.moshi)

    // Retrofit
    implementation(libs.bundles.retrofit)

    // Reactive
    implementation(libs.bundles.reactive)

    // Image tools
    ksp(libs.glide.ksp)
    implementation(libs.bundles.image.tools)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}