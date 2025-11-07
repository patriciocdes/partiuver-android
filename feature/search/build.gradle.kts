plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)

    jacoco
}

android {
    namespace = "com.partiuver.feature.search"

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

        debug {
            // Gera coverage nos instrumentados
            isTestCoverageEnabled = true
        }
    }

    testOptions {
        // melhora estabilidade dos testes UI
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
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
    implementation(project(":domain"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    // Para coletar flows de effects
    testImplementation(libs.cash.turbine)
    testImplementation(project(":domain"))

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)
}

// ./gradlew clean :feature:search:testDebugUnitTest :feature:search:connectedDebugAndroidTest :feature:search:jacocoCombinedReport

jacoco {
    // Versão estável que funciona bem no Gradle 8.x
    toolVersion = "0.8.11"
}

val jacocoExcludes = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "**/*Hilt*.*",
    "**/*Dagger*.*",
    "**/theme/**"
)

fun uiExecutionData(): FileCollection = files(
    // unit test .exec – caminhos possíveis conforme AGP/Gradle
    fileTree(buildDir) {
        include(
            "jacoco/testDebugUnitTest.exec",
            "outputs/unit_test_code_coverage/**/testDebugUnitTest.exec"
        )
    },
    // instrumented .ec – varia conforme forma de execução (managed devices / connected)
    fileTree(buildDir) {
        include(
            "outputs/code_coverage/**/*.ec",
            "outputs/managed_device_code_coverage/**/*.ec",
            "outputs/connected/**/*.ec"
        )
    }
)

// Relatório COMBINADO (unit + instrumented)
tasks.register<JacocoReport>("jacocoCombinedReport") {
    // garante que ambos rodam antes
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    group = "verification"
    description = "Gera relatório Jacoco combinado (unit + instrumented) para :core:ui"

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // classes compiladas do Android Library (debug)
    val kotlinDebug = fileTree("$buildDir/tmp/kotlin-classes/debug")
    val javaDebug   = fileTree("$buildDir/intermediates/javac/debug/classes")

    classDirectories.setFrom(
        files(kotlinDebug, javaDebug).asFileTree.matching {
            exclude(jacocoExcludes)
        }
    )
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    // Junta unit + instrumented (Jacoco faz o merge automaticamente)
    executionData.setFrom(uiExecutionData())

    // Log rápido
    doFirst {
        val execs = uiExecutionData().files
        println(">> JacocoCombinedReport — executionData encontrados:")
        execs.forEach { f -> println("   - ${f.path} (${if (f.exists()) f.length() else 0} bytes)") }
        if (execs.isEmpty()) {
            println(">> [AVISO] Nenhum arquivo de cobertura encontrado. Rode os testes antes.")
        }
    }
}