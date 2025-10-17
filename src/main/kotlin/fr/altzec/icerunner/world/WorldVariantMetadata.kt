package fr.altzec.fr.altzec.icerunner.world

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
    val yellowIslandCoordinates: Location,
    val yellowIslandGlassCoordinates: List<Location>,
) {
    override fun toString(): String = "Map center: $mapCenterCoordinates,\n" +
        "Amount of glass blocks found : ${mapCenterGlassCoordinates.size},\n" +
        "Green Island: $greenIslandCenterCoordinates,\n" +
        "Amount of glass blocks found : ${greenIslandGlassCoordinates.size},\n" +
        "Yellow Island: $yellowIslandCoordinates,\n" +
        "Amount of glass blocks found : ${yellowIslandGlassCoordinates.size}"

    companion object {
        private const val METADATA_FILE_NAME: String = "metadata.yaml"

        private const val MAIN_ISLAND_SYMBOLIC_NAME: String = "main_island"
        private const val YELLOW_ISLAND_SYMBOLIC_NAME: String = "yellow_island"
        private const val GREEN_ISLAND_SYMBOLIC_NAME: String = "green_island"

        private const val CENTER_COORDINATES_SYMBOLIC_NAME: String = "center_coordinates"
        private const val GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME: String = "glass_coordinates"

        // ------------- MAP MAIN ISLAND ------------- //
        private const val MAP_CENTER_COORDINATES_PATH = "$MAIN_ISLAND_SYMBOLIC_NAME.$CENTER_COORDINATES_SYMBOLIC_NAME"
        private const val MAP_CENTER_GLASS_BLOCKS_COORDINATES_PATH = "$MAIN_ISLAND_SYMBOLIC_NAME.$GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME"

        // ------------- GREEN SECONDARY ISLAND ------------- //
        private const val GREEN_ISLAND_CENTER_COORDINATES_PATH = "$GREEN_ISLAND_SYMBOLIC_NAME.$CENTER_COORDINATES_SYMBOLIC_NAME"
        private const val GREEN_ISLAND_GLASS_BLOCKS_COORDINATES_PATH = "$GREEN_ISLAND_SYMBOLIC_NAME.$GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME"

        // ------------- YELLOW SECONDARY ISLAND ------------- //
        private const val YELLOW_ISLAND_CENTER_COORDINATES_PATH = "$YELLOW_ISLAND_SYMBOLIC_NAME.$CENTER_COORDINATES_SYMBOLIC_NAME"
        private const val YELLOW_ISLAND_GLASS_BLOCKS_COORDINATES_PATH = "$YELLOW_ISLAND_SYMBOLIC_NAME.$GLASS_BLOCKS_COORDINATES_SYMBOLIC_NAME"

        fun loadWorldVariantMetadata(folder: File): WorldVariantMetadata {
            val file: File = File(folder.absolutePath + File.separator + METADATA_FILE_NAME)
            val yaml: FileConfiguration = YamlConfiguration.loadConfiguration(file)

            return WorldVariantMetadata(
                mapCenterCoordinates = yaml.getLocation(MAP_CENTER_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $MAP_CENTER_COORDINATES_PATH path in map variant metadata"),
                mapCenterGlassCoordinates = (yaml.getList(MAP_CENTER_GLASS_BLOCKS_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $MAP_CENTER_GLASS_BLOCKS_COORDINATES_PATH path in map variant metadata")) as List<Location>,
                greenIslandCenterCoordinates = yaml.getLocation(GREEN_ISLAND_CENTER_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $GREEN_ISLAND_CENTER_COORDINATES_PATH path in map variant metadata"),
                greenIslandGlassCoordinates = (yaml.getList(GREEN_ISLAND_GLASS_BLOCKS_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $GREEN_ISLAND_GLASS_BLOCKS_COORDINATES_PATH path in map variant metadata")) as List<Location>,
                yellowIslandCoordinates = yaml.getLocation(YELLOW_ISLAND_CENTER_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $YELLOW_ISLAND_CENTER_COORDINATES_PATH path in map variant metadata"),
                yellowIslandGlassCoordinates = (yaml.getList(YELLOW_ISLAND_GLASS_BLOCKS_COORDINATES_PATH) ?: throw IllegalStateException("Unable to read $YELLOW_ISLAND_GLASS_BLOCKS_COORDINATES_PATH path in map variant metadata")) as List<Location>,
            )
        }
    }
}
