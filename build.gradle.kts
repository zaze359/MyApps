@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
}

ext {
//    set("versionCode", 1)
//    set("versionName", "1.0")
//    set("kotlin_version", "1.7.20")
//    set("hiltVersion", "2.44")

//    kotlin_version = "1.5.21"
//    navigationVersion = "2.3.5"
//    hiltVersion = "2.38.1"
//    coroutinesVersion = "1.4.3"
//    lifecycle = "2.3.1"
}

buildscript {

    repositories {
        if (extra.has("useLocalMaven") && (extra["useLocalMaven"] as String).toBoolean()) {
            maven {
                isAllowInsecureProtocol = true
                url = uri("http://localhost:8081/repository/maven-public")
            }
        }
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
//        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
//        maven { url = uri("https://s01.oss.sonatype.org/content/groups/public") }
//        maven { url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2") }
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }

    dependencies {
//        classpath(libs.android.gradlePlugin)
//        classpath(libs.kotlin.gradlePlugin)
//        classpath(libs.hilt.gradlePlugin)
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


//subprojects {
//    repositories {
//        if(extra.has("useLocalMaven") && (extra["useLocalMaven"] as String).toBoolean()) {
//            maven {
//                isAllowInsecureProtocol = true
//                url = uri("http://localhost:8081/repository/maven-public")
//            }
//        }
//        maven { url = uri("https://maven.aliyun.com/repository/public") }
//        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
//        maven { url = uri("https://maven.aliyun.com/repository/google") }
//        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
//        maven { url = uri("https://jitpack.io") }
//        google()
//        mavenCentral()
//    }
//}