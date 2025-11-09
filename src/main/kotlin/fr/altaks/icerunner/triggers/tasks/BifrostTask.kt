package fr.altaks.icerunner.triggers.tasks

import fr.altaks.icerunner.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.NumberConversions

class BifrostTask(val main: Main) : BukkitRunnable() {

    companion object {
        private const val ISLAND_PROTECTION_RADIUS = 4.0
        private const val PLAYER_PROTECTION_RADIUS = 2.0
    }

    override fun run() {
        // Get snowball of the main world
        Bukkit.getWorlds().forEach { world ->
            world
                ?.entities
                ?.filter { entity -> entity.type == EntityType.SNOWBALL }
                ?.forEach { snowball ->
                    run {
                        // get velocity vector
                        var velocity = snowball.velocity

                        // normalize vector
                        // multiply *-1
                        velocity = velocity.normalize()
                        velocity = velocity.multiply(-3)

                        // get snowball pos
                        var position = snowball.location

                        // apply vector to position
                        position = position.add(velocity)
                        position = if (velocity.y >= 0.0) {
                            position.add(0.0, -1.0, 0.0)
                        } else {
                            position
                        }

                        // list of 4 blocks anchored in the base location
                        listOf(
//                            position.block,                                     position.block.getRelative(BlockFace.EAST),
//                            position.block.getRelative(BlockFace.SOUTH), position.block.getRelative(BlockFace.SOUTH_EAST),

                            position.block,
                            position.block.getRelative(BlockFace.NORTH),
                            position.block.getRelative(BlockFace.SOUTH),
                            position.block.getRelative(BlockFace.EAST),
                            position.block.getRelative(BlockFace.WEST),
                        ).forEach { block ->
                            run {
                                // If there are some players near the block, don't place it
                                if (isNearAPlayer(block.location) || isNearAnIsland(block.location)) return@run

                                // Replace Materials by match
                                val newType = when (block.type) {
                                    Material.AIR -> Material.ICE
                                    Material.ICE -> Material.PACKED_ICE
                                    Material.PACKED_ICE -> Material.BLUE_ICE
                                    else -> return@run
                                }

                                block.setType(newType, false)
                            }
                        }
                    }
                }
        }
    }

    private fun isNearAPlayer(location: Location): Boolean = location.world?.getNearbyEntities(location, PLAYER_PROTECTION_RADIUS, PLAYER_PROTECTION_RADIUS, PLAYER_PROTECTION_RADIUS) { entity -> entity.type == EntityType.PLAYER }?.isNotEmpty() ?: false

    private fun isNearAnIsland(location: Location): Boolean = this.main.worldManager.getIslandsCentersCoordinates().any { center -> center.distanceSquared(location) <= NumberConversions.square(ISLAND_PROTECTION_RADIUS) }
}
