package fr.altaks.icerunner.triggers.listeners

import fr.altaks.icerunner.Main
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.NumberConversions

class ArrowListener(val main: Main) : Listener {

    companion object {
        private const val EXPLOSION_RADIUS = 3
    }

    @EventHandler
    fun onArrowCollidesBlock(event: ProjectileHitEvent) {
        if (event.entity.type == EntityType.ARROW) {
            if (event.hitBlock != null) {
                event.entity.remove()

                // 3d radius matrix
                for (x in -EXPLOSION_RADIUS..EXPLOSION_RADIUS) {
                    for (y in -EXPLOSION_RADIUS..EXPLOSION_RADIUS) {
                        for (z in -EXPLOSION_RADIUS..EXPLOSION_RADIUS) {
                            // scanned block position
                            val position = event.hitBlock?.location?.add(x.toDouble(), y.toDouble(), z.toDouble()) ?: continue

                            // if the scanned block is in the sphere of radius EXPLOSION_RADIUS
                            if (position.distanceSquared(event.hitBlock?.location!!) <= NumberConversions.square(EXPLOSION_RADIUS.toDouble())) {
                                // Apply block replacing
                                val newType = when (position.block.type) {
                                    Material.ICE -> Material.AIR
                                    Material.PACKED_ICE -> Material.ICE
                                    Material.BLUE_ICE -> Material.PACKED_ICE
                                    else -> continue
                                }

                                position.block.setType(newType, false)
                            }
                        }
                    }
                }

                val explosionCenter = event.hitBlock?.location ?: throw IllegalStateException("Hit block does not have a Location")
                event.hitBlock?.world?.playSound(explosionCenter, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 50f, 1f)
                event.hitBlock?.world?.spawnParticle(Particle.GUST_EMITTER_LARGE, explosionCenter, 1)
            }
        }
    }
}
