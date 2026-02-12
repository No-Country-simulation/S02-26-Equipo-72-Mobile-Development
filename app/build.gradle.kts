plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    jacoco
}

android {
    namespace = "com.store.riderfit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.store.riderfit"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Firebase services (sin KTX - fue deprecado en julio 2025)
    implementation("com.google.firebase:firebase-auth:23.2.1")
    implementation("com.google.firebase:firebase-firestore:25.1.4")
    implementation("com.google.firebase:firebase-analytics:22.5.0")

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// JaCoCo configuration
jacoco {
    version = "0.8.10"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    classDirectories.setFrom(
        fileTree("${buildDir}/intermediates/classes/debug") {
            exclude(
                // Android Framework
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",

                // Código generado por herramientas
                "**/*Hilt*.class",
                "**/dagger/hilt/**",
                "**/hilt_aggregated_deps/**",
                "**/*_MembersInjector.class",
                "**/*_Factory.class",
                "**/*_Provide*Factory.class",

                // AndroidX - Todas las librerías
                "**/androidx/**",

                // Google Play Services y Firebase
                "**/com/google/android/gms/**",
                "**/com/google/firebase/**",
                "**/com/google/android/material/**",

                // Android System
                "**/android/**",

                // Kotlin y Java system
                "**/kotlin/**",
                "**/java/**",
                "**/javax/**",

                // Room generado automáticamente
                "**/data/local/database/dao/**",
                "**/*_Impl.class",

                // Firebase services (código de terceros)
                "**/data/remote/firebase/**",

                // Utils y configuración (bajo valor para testing)
                "**/utils/**",
                "**/theme/**",
                "**/navigation/**",
                "**/di/**",

                // UI Components (se testean en androidTest)
                "**/presentation/ui/**",

                // Modelos y DTOs (sin lógica compleja)
                "**/data/model/**",
                "**/data/local/database/entity/**",
                "**/domain/model/**",

                // Tests y archivos de test
                "**/*Test*.class",
                "**/*Tests*.class",
                "**/test/**",
                "**/androidTest/**"
            )
        }
    )

    sourceDirectories.setFrom(
        files(
            "${project.projectDir}/src/main/java",
            "${project.projectDir}/src/main/kotlin"
        )
    )

    executionData.setFrom(
        fileTree(buildDir) {
            include("jacoco/testDebugUnitTest.exec")
        }
    )

    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}
