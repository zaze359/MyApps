plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.zaze.apps"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

//        testInstrumentationRunnerArguments clearPackageData: 'true'
//        multiDexEnabled = true
//        javaCompileOptions { annotationProcessorOptions { includeCompileClasspath = true } }
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("android_demo.keystore")
            storePassword = "123456"
            keyAlias = "android"
            keyPassword = "123456"
        }
        create("release") {
            storeFile = file("android_demo.keystore")
            storePassword = "123456"
            keyAlias = "android"
            keyPassword = "123456"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kapt {
        correctErrorTypes = true
        arguments {
            arg("AROUTER_MODULE_NAME", project.name)
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            // 测试依赖于资源时开启，Android Studio 3.4 及更高版本默认提供编译版本的资源
            isIncludeAndroidResources = true
        }
    }

}

configurations.all {
    // check for updates every build
    resolutionStrategy.cacheChangingModulesFor(1, "seconds")
    resolutionStrategy.cacheDynamicVersionsFor(1, "seconds")
}


dependencies {
    testImplementation(project(":core:testing"))

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)


    // region hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.ext.compiler)
    // endregion hilt
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.android.material)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    //
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation(libs.androidx.compose.runtime.livedata)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Android Studio Preview support
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.compose.ui.googlefonts)

    implementation(libs.androidx.dataStore.core)
    implementation(libs.androidx.dataStore.preferences)

    implementation(project(":core:common"))
    implementation(project(":core:router"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:message"))

    implementation(project(":core:designsystem"))


//    implementation(libs.arouter.api)
//    kapt(libs.arouter.compiler)

//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
//    implementation 'androidx.core:core-ktx:1.3.2'
//    implementation 'androidx.appcompat:appcompat:1.2.0'
//    implementation 'com.google.android.material:material:1.3.0'
//    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
//    implementation 'androidx.legacy:legacy-support-v4:1.0.0'

//
//    androidTestImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
//    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
//    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"

//    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
//    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle"
//    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
//    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle"

//    implementation 'io.github.zaze359:zaze-common:1.0.0-SNAPSHOT'

//    implementation(project(":core:designsystem"))


//    implementation 'pub.devrel:easypermissions:3.0.0'
}
