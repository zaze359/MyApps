pluginManagement {
    val localPropertiesFile = File(rootProject.projectDir, "local.properties")
    val properties = java.util.Properties()
    properties.load(java.io.DataInputStream(localPropertiesFile.inputStream()))
    extra["useLocalMaven"] = properties.getProperty("useLocalMaven", "false").toBoolean()
    repositories {
        if (extra["useLocalMaven"] == true) {
            maven {
                isAllowInsecureProtocol = true
                url = uri("http://localhost:8081/repository/maven-public")
            }
        }
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        if (extra["useLocalMaven"] == true) {
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
include(":feature:message")
include(":core:designsystem")
include(":core:router")
