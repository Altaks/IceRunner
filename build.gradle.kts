import io.github.klahap.dotenv.DotEnvBuilder
import io.typst.spigradle.lombok
import io.typst.spigradle.spigot.Load
import io.typst.spigradle.spigot.spigot
import io.typst.spigradle.spigot.spigotmc

plugins {
    id("idea")
    id("org.jetbrains.kotlin.jvm") version "2.2.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.typst.spigradle") version "3.0.5"
    id("io.github.klahap.dotenv") version "1.1.3"
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

group = "fr.altzec"
version = "1.0-SNAPSHOT"

tasks.compileJava.get().options.encoding = "UTF-8"

repositories {
    mavenCentral()
    spigotmc()
}

dependencies {
    implementation(kotlin("stdlib-jdk8")) // Maybe you need to apply the plugin 'shadowJar' for shading 'kotlin-stdlib'.
    implementation(lombok())

    compileOnly(spigot(version = "1.21.8"))

    testImplementation("junit:junit:4.13.1")
    testImplementation(kotlin("stdlib-jdk8"))
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

spigot {
    authors = listOf("Altaks", "_Zecross_")
    apiVersion = "1.21"
    load = Load.STARTUP
    commands {
        create("dev") {
            aliases = listOf("development")
            description = "Developer debug command"
            permission = "dev.debug"
            permissionMessage = "You do not have permission!"
            usage = "/dev [startGame|endGame]"
        }
    }
    permissions {
//        create("test.foo") {
//            description = "Allows foo command"
//            defaults = "true"
//        }
//        create("test.*") {
//            description = "Wildcard permission"
//            defaults = "op"
//            children = mapOf("test.foo" to true)
//        }
    }
}

val envVars =
    DotEnvBuilder.dotEnv {
        addFileIfExists("$rootDir/.env")
    }

tasks.jar {
    destinationDirectory.set(File(envVars.getOrDefault("PLUGINS_DIRECTORY", "$rootDir/artifacts")))
}

tasks.shadowJar {
    destinationDirectory.set(File(envVars.getOrDefault("PLUGINS_DIRECTORY", "$rootDir/artifacts")))
}

tasks.processResources {
    from("src/main/resources")
    into("build/resources/main")
}

/*
tasks.named<KotlinJvmCompile>("compileKotlin") {
    compilerOptions {
        freeCompilerArgs.addAll("-Xno-call-assertions", "-Xno-receiver-assertions", "-Xno-param-assertions")
    }
}
*/
