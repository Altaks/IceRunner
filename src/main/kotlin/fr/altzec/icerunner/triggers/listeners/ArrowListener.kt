package fr.altzec.fr.altzec.icerunner.triggers.listeners

import fr.altzec.fr.altzec.icerunner.Main
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent


class ArrowListener(val main: Main) : Listener {
    @EventHandler
    fun arrowCollideBlock(event: ProjectileHitEvent){
        if (event.entity.type == EntityType.ARROW){
            val projectilePosition = event.hitBlock?.location ?:return
            for (x in -3..3){
                for(y in -3..3){
                   for(z in -3..3){
                        main.pluginLogger.info("block scanné =$x $y $z")
                        val position = projectilePosition.add(x.toDouble(), y.toDouble(), z.toDouble())
                        main.pluginLogger.info("$position")
                        val newType = when (position.block.type) {
                            Material.ICE -> Material.AIR
                            Material.PACKED_ICE -> Material.ICE
                            Material.BLUE_ICE -> Material.PACKED_ICE
                            else -> position.block.type
                        }
                        position.block.setType(newType, false)
                    }
                }
            }
        }
    }
}


