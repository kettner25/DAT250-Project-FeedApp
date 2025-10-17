plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    group = "no.hvl.group17"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

