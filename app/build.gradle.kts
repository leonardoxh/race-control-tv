plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "com.github.leonardoxh.f1"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 24
        versionName = "2.2.0"

        buildConfigField("String", "DEFAULT_USER_AGENT", "\"RaceControl f1viewer\"")
    }

    signingConfigs {
        create("release") {
            storeFile = project.properties["signing.key.store.path"]?.let { file(it) }
            storePassword = project.properties["signing.key.password"] as String?
            keyAlias = project.properties["signing.key.alias"] as String?
            keyPassword = project.properties["signing.key.password"] as String?
            isV1SigningEnabled = true
            isV2SigningEnabled = true
        }
    }

    buildTypes {
        val appName = "F1 TV Player"
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
            resValue("string", "app_name", "$appName (debug)")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            resValue("string", "app_name", appName)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}

dependencies {
    val kotlinCoroutinesVersion = "1.4.3"
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.3.3")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    val hiltVersion = rootProject.extra["hiltVersion"]
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    val okHttpVersion = "4.9.1"
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    val moshiVersion = "1.9.3"
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion") {
        exclude(module = "kotlin-reflect")
    }
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation("com.auth0.android:jwtdecode:2.0.0")

    val glideVersion = "4.11.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    val exoplayerVersion = "2.13.2"
    implementation("com.google.android.exoplayer:exoplayer:$exoplayerVersion")
    implementation("com.google.android.exoplayer:extension-leanback:$exoplayerVersion")
    implementation("com.google.android.exoplayer:extension-okhttp:$exoplayerVersion")

    val roomVersion = "2.2.5"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("com.google.android.material:material:1.3.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.0")
}

kapt {
    correctErrorTypes = true
}
