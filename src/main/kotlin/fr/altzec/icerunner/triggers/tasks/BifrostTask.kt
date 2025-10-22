package fr.altzec.fr.altzec.icerunner.triggers.tasks

import fr.altzec.fr.altzec.icerunner.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable

class BifrostTask : BukkitRunnable() {
    override fun run() {
        // Get snowball of the main world
        Bukkit.getWorlds().forEach { world ->
            world
                ?.entities
                ?.filter { entity -> entity.type == EntityType.SNOWBALL }
                ?.forEach { snowball ->
                    run {
                        // get velocity vector
                        var velocity = snowball.velocity;

                        // normalize vector
                        // multiply *-1
                        velocity = velocity.normalize()
                        velocity = velocity.multiply(-3)

                        // get snowball pos
                        var position = snowball.location

                        // apply vector to position
                        position = position.add(velocity)
                        if (velocity.y > 0.0) {
                            position = position.add(0.0, -2.0, 0.0)
                        }

                        // list of 4 blocks anchored in the base location
                        listOf(
//                            position.block,                                     position.block.getRelative(BlockFace.EAST),
//                            position.block.getRelative(BlockFace.SOUTH), position.block.getRelative(BlockFace.SOUTH_EAST),


                            position.block,
                            position.block.getRelative(BlockFace.NORTH), position.block.getRelative(BlockFace.SOUTH),
                            position.block.getRelative(BlockFace.EAST), position.block.getRelative(BlockFace.WEST),
                        ).forEach { block -> run {

                            // If there are some players near the block, don't place it
                            block.world.getNearbyEntities(position, 1.0, 1.0, 1.0) { entity -> entity.type == EntityType.PLAYER }
                                .isEmpty()
                                .let {
                                    if(!it) {
                                        return;
                                    }
                                }

                            // Replace Materials by match
                            val newType = when (block.type) {
                                Material.AIR -> Material.ICE
                                Material.ICE -> Material.PACKED_ICE
                                Material.PACKED_ICE -> Material.BLUE_ICE
                                else -> position.block.type
                            }

                            block.setType(newType, false)
                        } }
                    }
                }
        }
    }
}