plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.1")
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    mainClass.set("App")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}