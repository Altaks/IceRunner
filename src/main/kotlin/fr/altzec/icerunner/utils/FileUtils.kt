package fr.altzec.fr.altzec.icerunner.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.jvm.java

/**
 * @author Altaks
 */
class FileUtils {

    companion object {

        // In companion object for static function access

        /**
         * Recursively copies files from an "in JAR" resource folder to an external directory
         * @param resourcePath : The chosen "in JAR" resource folder to copy
         * @param targetDir : The target directory where the files will be copied
         */
        fun copyResourceDir(resourcePath: String, targetDir: String) {
            val classLoader = FileUtils::class.java.classLoader
            val resourceUrl = classLoader.getResource(resourcePath)
                ?: throw IllegalArgumentException("Resource $resourcePath not found!")

            // Create target directory if it doesn't exist
            val targetPath = Paths.get(targetDir)
            Files.createDirectories(targetPath)

            // List all files in the resource directory
            val resourceRoot = if (resourceUrl.protocol == "jar") {
                // Handle JAR resources
                val jarPath = resourceUrl.path.substring(5, resourceUrl.path.indexOf("!"))
                val jarFile = File(jarPath)
                val fileSystem = java.nio.file.FileSystems.newFileSystem(jarFile.toPath())
                fileSystem.getPath(resourcePath)
            } else {
                // Handle file system resources (for testing)
                Paths.get(resourceUrl.toURI())
            }

            // Walk through the resource directory
            Files.walk(resourceRoot).forEach { source ->
                val relative = resourceRoot.relativize(source)
                val target = targetPath.resolve(relative.toString())

                if (Files.isDirectory(source)) {
                    Files.createDirectories(target)
                } else {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }


}