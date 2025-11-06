package fr.altaks.icerunner.world

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

data class WorldVariantMetadata(
    // ------------- MAP MAIN ISLAND ------------- //
    val mapCenterCoordinates: Location,
    val mapCenterGlassCoordinates: List<Location>,

    // ------------- GREEN SECONDARY ISLAND ------------- //
    val greenIslandCenterCoordinates: Location,
    val greenIslandGlassCoordinates: List<Location>,

    // ------------- YELLOW SECONDARY ISLAND ------------- //
    val yellowIslandCenterCoordinates: Location,
    val yellowIslandGlassCoordinates: List<Location>,

    // ------------- RED TEAM ISLAND ------------- //
    val redTeamSpawnCoordinates: Location,

    // ------------- BLUE TEAM ISLAND ------------- //
    val blueTeamSpawnCoordinates: Location,
) {
    override fun toString(): String = "Map center: $mapCenterCoordinates,\n" +
        "Amount of glass blocks found : ${mapCenterGlassCoordinates.size},\n" +
        "Green Island: $greenIslandCenterCoordinates,\n" +
        "Amount of glass blocks found : ${greenIslandGlassCoordinates.size},\n" +
        "Yellow Island: $yellowIslandCenterCoordinates,\n" +
        "Amount of glass blocks found : ${yellowIslandGlassCoordinates.size}"

    companion object {
        private const val METADATA_FILE_NAME: String = "metadata.yaml"

        private const val MAIN_ISLAND_SYMBOLIC_NAME: String = "main_island"
        private const val YELLOW_ISLAND_SYMBOLIC_NAME: String = "yellow_island"
        private const val GREEN_ISLAND_SYMBOLIC_NAME: String = "green_island"

        private const val CENTER_COORDINATES_SYMBOLIC_NAME: String = "center_coordinates"
        private const val GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME: String = "glass_coordinates"

        private const val RED_TEAM_ISLAND_SYMBOLIC_NAME: String = "red_island"
        private const val BLUE_TEAM_ISLAND_SYMBOLIC_NAME: String = "blue_island"

        private const val TEAM_SPAWN_COORDINATES_SYMBOLIC_NAME: String = "team_spawn_coordinates"

        // ------------- MAP MAIN ISLAND ------------- //
        private const val MAP_CENTER_COORDINATES_PATH = "$MAIN_ISLAND_SYMBOLIC_NAME.$CENTER_COORDINATES_SYMBOLIC_NAME"
        private const val MAP_CENTER_GLASS_BLOCKS_COORDINATES_PATH = "$MAIN_ISLAND_SYMBOLIC_NAME.$GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME"

        // ------------- GREEN SECONDARY ISLAND ------------- //
        private const val GREEN_ISLAND_CENTER_COORDINATES_PATH = "$GREEN_ISLAND_SYMBOLIC_NAME.$CENTER_COORDINATES_SYMBOLIC_NAME"
        private const val GREEN_ISLAND_GLASS_BLOCKS_COORDINATES_PATH = "$GREEN_ISLAND_SYMBOLIC_NAME.$GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME"

        // ------------- YELLOW SECONDARY ISLAND ------------- //
        private const val YELLOW_ISLAND_CENTER_COORDINATES_PATH = "$YELLOW_ISLAND_SYMBOLIC_NAME.$CENTER_COORDINATES_SYMBOLIC_NAME"
        private const val YELLOW_ISLAND_GLASS_BLOCKS_COORDINATES_PATH = "$YELLOW_ISLAND_SYMBOLIC_NAME.$GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME"

        // ------------- RED TEAM ISLAND ------------- //
        private const val RED_TEAM_SPAWN_COORDINATES_PATH = "$RED_TEAM_ISLAND_SYMBOLIC_NAME.$TEAM_SPAWN_COORDINATES_SYMBOLIC_NAME"

        // ------------- BLUE TEAM ISLAND ------------- //
        private const val BLUE_TEAM_SPAWN_COORDINATES_PATH = "$BLUE_TEAM_ISLAND_SYMBOLIC_NAME.$TEAM_SPAWN_COORDINATES_SYMBOLIC_NAME"

        fun loadWorldVariantMetadata(folder: File): WorldVariantMetadata {
            val file = File(folder.absolutePath + File.separator + METADATA_FILE_NAME)
            val yaml: FileConfiguration = YamlConfiguration.loadConfiguration(file)

            @Suppress("UNCHECKED_CAST")
            return WorldVariantMetadata(
                mapCenterCoordinates = yaml.getLocation(MAP_CENTER_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $MAP_CENTER_COORDINATES_PATH path in map variant metadata"),
                mapCenterGlassCoordinates = (yaml.getList(MAP_CENTER_GLASS_BLOCKS_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $MAP_CENTER_GLASS_BLOCKS_COORDINATES_PATH path in map variant metadata")) as List<Location>,
                greenIslandCenterCoordinates = yaml.getLocation(GREEN_ISLAND_CENTER_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $GREEN_ISLAND_CENTER_COORDINATES_PATH path in map variant metadata"),
                greenIslandGlassCoordinates = (yaml.getList(GREEN_ISLAND_GLASS_BLOCKS_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $GREEN_ISLAND_GLASS_BLOCKS_COORDINATES_PATH path in map variant metadata")) as List<Location>,
                yellowIslandCenterCoordinates = yaml.getLocation(YELLOW_ISLAND_CENTER_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $YELLOW_ISLAND_CENTER_COORDINATES_PATH path in map variant metadata"),
                yellowIslandGlassCoordinates = (yaml.getList(YELLOW_ISLAND_GLASS_BLOCKS_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $YELLOW_ISLAND_GLASS_BLOCKS_COORDINATES_PATH path in map variant metadata")) as List<Location>,
                redTeamSpawnCoordinates = yaml.getLocation(RED_TEAM_SPAWN_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $RED_TEAM_SPAWN_COORDINATES_PATH path in map variant metadata"),
                blueTeamSpawnCoordinates = yaml.getLocation(BLUE_TEAM_SPAWN_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $BLUE_TEAM_SPAWN_COORDINATES_PATH path in map variant metadata"),
            )
        }
    }
}
