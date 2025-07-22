plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("com.google.firebase:firebase-admin:9.2.0")
}

application {
    mainClass.set("com.fitness.FitnessApp")

    applicationDefaultJvmArgs = listOf(
        "-Djava.library.path=src/main/resources"
    )
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
