package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemComparator
import fr.altaks.icerunner.utils.ItemFactory
import fr.altaks.icerunner.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.NumberConversions
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

class ShopLastJudgement(val main: Main) : ShopManager.Companion.IShopItem {

    companion object {
        private const val EXPLOSION_EFFECT_RADIUS = 40
        private val EXPLOSION_AFFECTED_BLOCKS = listOf<Material>(Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE)
    }

    override fun cost(): UInt = 30u
    override fun item(): ItemStack = ItemFactory(Material.BLAZE_POWDER, 1)
        .setDisplayName("${ChatColor.GOLD}⚡ Jugement dernier")
        .setLore(
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GRAY}La poudre enflammée du Feu éternel. En la lançant, tu",
            "${ChatColor.GRAY}invoques le jugement des anciens dieux, un cataclysme",
            "${ChatColor.GRAY}déchaînant la fureur du feu céleste, consumant",
            "${ChatColor.GRAY}toute construction et ennemis sur son passage.",
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GOLD}⛁ ${cost()} ${
                if (cost() <= 1u) {
                    "pièce"
                } else {
                    "pièces"
                }
            }",
        )
        .build()

    override fun position(): Int = 8

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        if (event.item != null && ItemComparator.compare(event.item, item())) {
            event.isCancelled = true
            if(activeLastJudgement.isEmpty()) {
                event.player.world.time = WorldManager.MIDNIGHT_TIME_TICKS
            }
            triggerLastJudgement(event.player)
            GameItems.decrementHeldItemAmount(event.player)
        }
    }

    private val activeLastJudgement = mutableListOf<UUID>()

    private fun triggerLastJudgement(player: Player) {
        activeLastJudgement.add(player.uniqueId)
        LastJudgementTask(player.uniqueId, player.world, main, this.activeLastJudgement).runTaskTimer(this.main, 0, 5L)
    }

    private class LastJudgementTask(val playerUUID: UUID, val world: World, val main: Main, val activeJudgements: MutableList<UUID>) : BukkitRunnable() {

        companion object {
            private const val HELIX_HEIGHT = 0.8 // default : 0.5
            private const val HELIX_RADIUS = 0.6 // default : 0.4

            private const val PARTICLE_AMOUNT = 1
            private const val PARTICLE_SPEED = 0.0
            private const val PARTICLE_LOCATION_DELTA = 0.0
        }

        var phi = 0.0

        override fun run() {
            if(!Bukkit.getOfflinePlayer(playerUUID).isOnline) {
                resetTimeToDayIfLastActiveLastJudgement(world)
                this.cancel()
            }

            val player = Bukkit.getPlayer(playerUUID) ?: throw IllegalStateException("Couldn't get player on LastJudgement task, however player is online !");

            phi += Math.PI / 8

            var x: Double
            var y: Double
            var z: Double

            var theta = 0.0

            while (theta <= 2.0 * Math.PI) {
                theta += Math.PI / 16

                for (i in 0..1) {
                    x = HELIX_RADIUS * (3 * Math.PI - theta) * HELIX_RADIUS * cos(theta + phi + i * Math.PI)
                    y = HELIX_HEIGHT * theta
                    z = HELIX_RADIUS * (3 * Math.PI - theta) * HELIX_RADIUS * sin(theta + phi + i * Math.PI)

                    val particleSpawnLocation = player.location.add(x, 5.0 - y, z)
                    particleSpawnLocation.world
                        ?.spawnParticle(
                            if (i % 2 == 0) {
                                Particle.SOUL_FIRE_FLAME
                            } else {
                                Particle.FLAME
                            },
                            particleSpawnLocation.x,
                            particleSpawnLocation.y,
                            particleSpawnLocation.z,
                            PARTICLE_AMOUNT,
                            PARTICLE_LOCATION_DELTA,
                            PARTICLE_LOCATION_DELTA,
                            PARTICLE_LOCATION_DELTA,
                            PARTICLE_SPEED,
                        )
                }
            }

            if (phi > 10 * Math.PI) {
                wipePlayersAndIceBridges(player, world)
                resetTimeToDayIfLastActiveLastJudgement(world)
                this.cancel()
            }

            player.location.world?.spawnParticle(Particle.FLAME, player.location, 250, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), 0.0)
            player.location.world?.spawnParticle(Particle.SOUL_FIRE_FLAME, player.location, 250, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), 0.0)
        }

        override fun cancel() {
            activeJudgements.remove(playerUUID)
            super.cancel()
        }

        fun wipePlayersAndIceBridges(player: Player, world: World) {
            for (x in -EXPLOSION_EFFECT_RADIUS..EXPLOSION_EFFECT_RADIUS) {
                for (y in -EXPLOSION_EFFECT_RADIUS..EXPLOSION_EFFECT_RADIUS) {
                    for (z in -EXPLOSION_EFFECT_RADIUS..EXPLOSION_EFFECT_RADIUS) {
                        // scanned block position
                        val position = player.location.add(x.toDouble(), y.toDouble(), z.toDouble())

                        // if the scanned block is in the sphere of radius EXPLOSION_EFFECT_RADIUS
                        if (position.distanceSquared(player.location) <= NumberConversions.square(EXPLOSION_EFFECT_RADIUS.toDouble())) {
                            if (EXPLOSION_AFFECTED_BLOCKS.contains(position.block.type)) {
                                position.block.setType(Material.AIR, false)
                            }
                        }
                    }
                }
            }

            world.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 50f, 1f)
            world.spawnParticle(Particle.EXPLOSION, player.location, 50, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), 0.1)

            world
                .getNearbyEntities(player.location, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble())
                .filter { entity -> entity.type == EntityType.PLAYER }
                .forEach { entity -> this.main.gameManager.respawnPlayer(entity as Player) }
        }

        fun resetTimeToDayIfLastActiveLastJudgement(world: World) {
            if(activeJudgements.size <= 1) {
                world.time = WorldManager.NOON_TIME_TICKS
            }
        }
    }
}
