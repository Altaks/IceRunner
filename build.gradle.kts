import io.github.klahap.dotenv.DotEnvBuilder
import io.typst.spigradle.jitpack
import io.typst.spigradle.spigot.Load
import io.typst.spigradle.spigot.paper
import io.typst.spigradle.spigot.papermc
import io.typst.spigradle.spigot.spigot
import io.typst.spigradle.spigot.spigotmc
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("idea")
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.2.2"
    id("io.typst.spigradle") version "3.1.2"
    id("io.github.klahap.dotenv") version "1.1.3"
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "fr.altaks"
version = "1.0.0"

tasks.compileJava.get().options.encoding = "UTF-8"

repositories {
    // Main GDKs repositories
    spigotmc()
    papermc()

    // Secondary libs repositories
    mavenCentral()
    jitpack()
}

dependencies {
    // Kotlin support
    implementation(kotlin("stdlib-jdk8"))

    // FastBoard & FastInv to easily manage scoreboard and inventories
    implementation("fr.mrmicky:fastboard:2.1.5")
    implementation("fr.mrmicky:fastinv:3.1.2")

    // SpigotMC
    compileOnly(spigot(version = "1.21.8"))

    // Testing - Kotlin & PaperMC
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(paper(version = "1.21.8"))

    // Testing - MockBukkit
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.89.0")

    // Testng - JUnit & Juniper platform
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

// Spigot `plugin.yml` configuration
spigot {
    authors = listOf("Altaks")
    apiVersion = "1.21"
    load = Load.POST_WORLD
    commands {
        create("dev") {
            aliases = listOf("development")
            description = "Developer debug command"
            permission = "dev.debug"
            permissionMessage = "You do not have permission!"
            usage = "/dev [startGame|endGame]"
        }
        create("all") {
            aliases = listOf("chatall")
            description = "Sends a message to all players during the game"
            usage = "/all <message>"
        }
    }
    permissions {
        create("dev.*") {
            description = "Allows for debugging related interactions"
            defaults = "op"
            children = mapOf("dev.debug" to true)
        }
    }
}

// Load environment variables from the directory dotenv file if it exists. File template being `.env.template`
val envVars =
    DotEnvBuilder.dotEnv {
        addFileIfExists("$rootDir/.env")
    }

tasks.jar {
    destinationDirectory.set(File(envVars.getOrDefault("PLUGINS_DIRECTORY", "$rootDir/artifacts")))
}

tasks.shadowJar {

    // Libraries relocations
    relocate("fr.mrmicky.fastboard", "$group.fastboard")
    relocate("fr.mrmicky.fastinv", "$group.fastinv")

    destinationDirectory.set(File(envVars.getOrDefault("PLUGINS_DIRECTORY", "$rootDir/artifacts")))

    // Remove the annoying "-all" suffix.
    archiveClassifier = ""
}

// Allow to include every non-spigot-directly-related-resource within the JAR resources
tasks.processResources {
    from("src/main/resources")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    into("build/resources/main")
}

// Custom config for testing
tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}

tasks.withType<DokkaTask>().configureEach {
    moduleVersion.set(project.version.toString())
    failOnWarning.set(true)
    suppressObviousFunctions.set(true)
    suppressInheritedMembers.set(false)
    offlineMode.set(false)
}

/* Removed kotlin compiler details
tasks.named<KotlinJvmCompile>("compileKotlin") {
    compilerOptions {
        freeCompilerArgs.addAll("-Xno-call-assertions", "-Xno-receiver-assertions", "-Xno-param-assertions")
    }
}
*/
