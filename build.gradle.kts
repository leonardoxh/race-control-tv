buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))

        val hiltVersion = "2.35.1"
        project.extra["hiltVersion"] = hiltVersion
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
