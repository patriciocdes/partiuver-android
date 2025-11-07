plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)

    jacoco
}

android {
    namespace = "com.partiuver.core.network"

    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.retrofit)
    testImplementation(libs.converter.gson)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

jacoco {
    // Versão estável que funciona bem no Gradle 8.x
    toolVersion = "0.8.11"
}

val jacocoExcludes = listOf(
    "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
    "**/*Test*.*",
    // DI/gerados (ajuste se quiser medir)
    "**/*Hilt*.*", "**/*Dagger*.*", "**/*Module*.*", "**/di/**"
)

// Relatório para a variante Debug (a mais usada em unit tests)
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest") // garante que os testes rodem antes

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // classes compiladas (Kotlin + Java) da variante debug
    val kotlinDebug = layout.buildDirectory.dir("tmp/kotlin-classes/debug")
    val javaDebug   = layout.buildDirectory.dir("intermediates/javac/debug/classes")

    classDirectories.setFrom(
        files(kotlinDebug, javaDebug).asFileTree.matching {
            exclude(jacocoExcludes)
        }
    )

    // fontes
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    // dados de execução gerados pelos unit tests da variante debug
    executionData.setFrom(
        layout.buildDirectory.file("jacoco/testDebugUnitTest.exec")
    )
}