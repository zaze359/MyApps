//apply(from = "${project.rootDir}/buildscripts/module.gradle.kts")
buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
//        classpath(libs.hilt.gradlePlugin)
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
}
// 处理 Expecting an expression 报错，下面添加一个任何语句
run {}

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