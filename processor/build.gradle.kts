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
            artifactId = "intent-processor"
            version = version.toString()
        }
    }
}

dependencies {
    implementation(project(":annotation"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.24")
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("javax.inject:javax.inject:1")
}