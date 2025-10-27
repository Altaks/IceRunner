package fr.altzec.fr.altzec.icerunner.triggers.tasks

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable

class ArrowTask : BukkitRunnable() {
    companion object {
        private const val PARTICLE_AMOUNT = 3
        private const val PARTICLE_SPEED = 0.1
        private const val PARTICLE_LOCATION_DELTA = 0.1
    }
    override fun run() {
        // Get arrow of the main world
        Bukkit.getWorlds().forEach { world ->
            world
                ?.entities
                ?.filter { entity -> entity.type == EntityType.ARROW }
                ?.forEach { arrow ->
                    run {
                        arrow.world.spawnParticle(
                            Particle.SOUL_FIRE_FLAME,
                            arrow.location.x,
                            arrow.location.y,
                            arrow.location.z,
                            PARTICLE_AMOUNT,
                            PARTICLE_LOCATION_DELTA,
                            PARTICLE_LOCATION_DELTA,
                            PARTICLE_LOCATION_DELTA,
                            PARTICLE_SPEED
                        )
                    }
                }
        }
    }
}
