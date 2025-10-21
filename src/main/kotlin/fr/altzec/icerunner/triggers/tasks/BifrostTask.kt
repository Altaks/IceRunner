package fr.altzec.fr.altzec.icerunner.triggers.tasks

import fr.altzec.fr.altzec.icerunner.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable

class BifrostTask : BukkitRunnable() {
    override fun run() {
        //get snowball of the main world
        Bukkit.getWorld(WorldManager.ICE_RUNNER_WORLD_NAME)?.entities
            ?.filter { entity -> entity.type == EntityType.SNOWBALL }
            ?.forEach { snowball ->
                run {
                    //get velocity vector
                    var velocity = snowball.velocity;
                    //normalize vector
                    velocity = velocity.normalize()
                    //multiply *-1
                    velocity = velocity.multiply(-2)
                    //get snowball pos
                    var position = snowball.location
                    //apply vector to position
                    position = position.add(velocity)
                    position=position.add(0.0,-2.0,0.0)
                    //Replace Materials by match
                    val newType = when (position.block.type) {
                        Material.AIR -> Material.ICE
                        Material.ICE -> Material.PACKED_ICE
                        Material.PACKED_ICE -> Material.BLUE_ICE
                        else -> position.block.type
                    }
                    position.block.setType(newType, false)
                }
            }
    }
}