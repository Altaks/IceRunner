package fr.altzec.fr.altzec.icerunner.world

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.utils.FileUtils
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.io.File

/**
 * @author Altaks
 */
class WorldManager(val main: Main) {

    companion object {
        // Get the folder that contains all the different worlds
        private const val ICE_RUNNER_WORLD_DESTINATION_PATH = "."
        private const val ICE_RUNNER_WORLD_NAME = "world_ice_runner"

        // All the worlds resources variants of the plugin
        private const val DEFAULT_WORLD_VARIANT_PATH = "worlds/variants/ice"

        // Some world configuration constants
        private const val NOON_TIME_TICKS = 6000L

        private const val MAIN_ISLAND_POPULATION_SCAN_RADIUS = 3.0
        private const val SECONDARY_ISLAND_POPULATION_SCAN_RADIUS = 2.0

        private const val Y_AXIS_POPULATION_SCAN_RADIUS = 1.5
    }

    enum class WorldIslands {
        CENTER,
        GREEN,
        YELLOW,
    }

    var loadedWorldMetadata: WorldVariantMetadata? = null

    /**
     * Generates the IceRunner game world, by copying it from the inside of the JAR to the outside filesystem
     */
    fun setupGameWorld() {
        generateGameWorld()
        loadGameWorld()
        configureGameWorldBehavior()
        teleportPlayersToGameWorld()
    }

    private fun generateGameWorld() {
        val worldDestination = "$ICE_RUNNER_WORLD_DESTINATION_PATH${File.separator}$ICE_RUNNER_WORLD_NAME"
        main.logger.info("Copying game world $DEFAULT_WORLD_VARIANT_PATH from JAR file to $worldDestination")
        FileUtils.copyResourceDir(DEFAULT_WORLD_VARIANT_PATH, worldDestination)
        main.logger.info("Finished copying game world to $worldDestination")
    }

    private fun loadGameWorld() {
        // We call `createWorld` with the world name because Bukkit loads the world if it already exists in the server files
        Bukkit.getServer().createWorld(WorldCreator(ICE_RUNNER_WORLD_NAME))

        val worldFolder = Bukkit.getServer().getWorld(ICE_RUNNER_WORLD_NAME)?.worldFolder
            ?: throw IllegalStateException("Loaded world $ICE_RUNNER_WORLD_NAME but couldn't resolve world folder")

        val metadata = WorldVariantMetadata.loadWorldVariantMetadata(worldFolder)

        this.loadedWorldMetadata = metadata
        this.main.logger.info(this.loadedWorldMetadata?.toString())
    }

    fun configureGameWorldBehavior() {
        val gameWorld = Bukkit.getWorld(ICE_RUNNER_WORLD_NAME) ?: throw IllegalStateException("World $ICE_RUNNER_WORLD_NAME not found")

        gameWorld.setGameRule(GameRule<Boolean>.DO_DAYLIGHT_CYCLE, false)
        gameWorld.setGameRule(GameRule<Boolean>.DO_WEATHER_CYCLE, false)
        gameWorld.setGameRule(GameRule<Boolean>.DO_FIRE_TICK, false)

        gameWorld.clearWeatherDuration = Int.MAX_VALUE
        gameWorld.time = NOON_TIME_TICKS
    }

    fun teleportPlayersToGameWorld() {
        val gameWorld: World = Bukkit.getWorld(ICE_RUNNER_WORLD_NAME) ?: throw IllegalStateException("World $ICE_RUNNER_WORLD_NAME not found!")
        Bukkit.getOnlinePlayers().forEach { player -> player.teleport(Location(gameWorld, 0.0, 100.5 + 1, 0.0)) }
    }

    fun getIslandsVisitors(): Map<WorldIslands, List<Player>> = WorldIslands.entries.associateWith { getNearbyPlayers(loadedWorldMetadata?.mapCenterCoordinates?.world!!, it) }

    private fun getNearbyPlayers(world: World, island: WorldIslands): List<Player> = when (island) {
        WorldIslands.CENTER -> world.getNearbyEntities(loadedWorldMetadata?.mapCenterCoordinates!!, MAIN_ISLAND_POPULATION_SCAN_RADIUS, Y_AXIS_POPULATION_SCAN_RADIUS, MAIN_ISLAND_POPULATION_SCAN_RADIUS)
        WorldIslands.GREEN -> world.getNearbyEntities(loadedWorldMetadata?.greenIslandCenterCoordinates!!, SECONDARY_ISLAND_POPULATION_SCAN_RADIUS, Y_AXIS_POPULATION_SCAN_RADIUS, SECONDARY_ISLAND_POPULATION_SCAN_RADIUS)
        WorldIslands.YELLOW -> world.getNearbyEntities(loadedWorldMetadata?.yellowIslandCenterCoordinates!!, SECONDARY_ISLAND_POPULATION_SCAN_RADIUS, Y_AXIS_POPULATION_SCAN_RADIUS, SECONDARY_ISLAND_POPULATION_SCAN_RADIUS)
    }.filter { entity -> entity.type == EntityType.PLAYER }.map { entity -> entity as Player }.toList()
}
