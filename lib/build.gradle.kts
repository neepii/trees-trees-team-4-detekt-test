plugins {
    alias(libs.plugins.kotlin.jvm)
    id("jacoco")
    id("java-library")
    id("org.jetbrains.dokka") version "2.0.0"
}

repositories {
    mavenCentral()
}

val detekt by configurations.creating

val detektTask = tasks.register<JavaExec>("detekt") {
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    classpath = detekt

    val input = projectDir
    val config = "$projectDir/detekt.yml"
    val exclude = ".*/build/.*,.*/resources/.*"
    val params = listOf("-i", input, "-c", config, "-ex", exclude)

    args(params)
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

		detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.8")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.check {
		dependsOn(detektTask)
}
