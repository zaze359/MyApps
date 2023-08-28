pluginManagement {
    repositories {
        if (extra.has("useLocalMaven") && (extra["useLocalMaven"] as String).toBoolean()) {
            maven {
                isAllowInsecureProtocol = true
                url = uri("http://localhost:8081/repository/maven-public")
            }
        }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    //
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
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}


rootProject.name = "MyApps"
include(":app")
include(":core:testing")
include(":core:common")
include(":feature:settings")