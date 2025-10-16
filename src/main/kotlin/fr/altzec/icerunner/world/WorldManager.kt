package fr.altzec.fr.altzec.icerunner.world

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.utils.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File

/**
 * @author Altaks
 */
class WorldManager(val main: Main) {

    companion object {
        // Get the folder that contains all the different worlds
        private val ICE_RUNNER_WORLD_DESTINATION_PATH = Bukkit.getWorldContainer().path;
        private const val ICE_RUNNER_WORLD_NAME = "world_ice_runner";

        // All the worlds resources variants of the plugin
        private const val DEFAULT_WORLD_VARIANT_PATH = "worlds/ice_runner_default_world_variant";
    }

    /**
     * Generates the IceRunner game world, by copying it from the inside of the JAR to the outside filesystem
     */
    fun setupGameWorld() {
        generateGameWorld()
        loadGameWorld()
        teleportPlayersToGameWorld()
    }

    fun teleportPlayersToGameWorld() {
        val gameWorld: World = Bukkit.getWorld(ICE_RUNNER_WORLD_NAME) ?: throw IllegalStateException("World $ICE_RUNNER_WORLD_NAME not found!");
        Bukkit.getOnlinePlayers().forEach { player -> player.teleport(Location(gameWorld, 0.0, 0.0, 0.0)) }
    }

    private fun generateGameWorld() {
        val worldDestination = "$ICE_RUNNER_WORLD_DESTINATION_PATH${File.separator}$ICE_RUNNER_WORLD_NAME";
        main.logger.info("Copying game world $DEFAULT_WORLD_VARIANT_PATH from JAR file to $worldDestination");
        FileUtils.copyResourceDir(DEFAULT_WORLD_VARIANT_PATH, worldDestination);
        main.logger.info("Finished copying game world to $worldDestination");
    }

    private fun loadGameWorld() {
        // We call `createWorld` with the world name because Bukkit loads the world if it already exists in the server files
        Bukkit.getServer().createWorld(WorldCreator(ICE_RUNNER_WORLD_NAME))
    }

}