plugins {
    kotlin("jvm") version "2.2.0"
    id("maven-publish")
}

group = "com.polydot"
version = "1.0.0"

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            groupId = group.toString()
            artifactId = "intent-annotation"
            version = version.toString()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")
}