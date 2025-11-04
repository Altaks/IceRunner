package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemComparator
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.NumberConversions

class ShopFireball(val main: Main) : ShopManager.Companion.IShopItem {

    companion object {
        private const val FIREBALL_MELT_EFFECT_RADIUS = 3
        private val FIREBALL_MELT_AFFECTED_BLOCKS = listOf<Material>(Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE)
    }

    override fun cost(): UInt = 4u
    override fun item(): ItemStack = ItemFactory(Material.FIRE_CHARGE, 1)
        .setDisplayName("${ChatColor.RED}\uD83D\uDD25 Souffle de flamme")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Essence enflammée du dragon Nidhögg, soufflée des abîmes. ",
            "${ChatColor.GRAY}Cette boule de feu, aux flammes ardentes, incinère tout sur",
            "${ChatColor.GRAY}son passage, défiant la glace, le vent, et l'adversité",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 4

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        if (event.item != null && ItemComparator.compare(event.item, item())) {
            event.isCancelled = true
            ensureFireballTaskIsActive()
            spawnAndShootFireball(event.player)
        }
    }

    @EventHandler
    fun onFireballHitsBlock(event: ProjectileHitEvent) {
        if (event.entity.type == EntityType.FIREBALL || event.entity.type == EntityType.SMALL_FIREBALL) {
            event.isCancelled = true
            event.entity.remove()
        }
    }

    @EventHandler
    fun onFireballExplodesOnGround(event: ExplosionPrimeEvent) {
        if (event.entity.type == EntityType.FIREBALL || event.entity.type == EntityType.SMALL_FIREBALL) {
            event.isCancelled = true
            event.entity.remove()
        }
    }

    private fun spawnAndShootFireball(player: Player) {
        val eyePosition = player.eyeLocation
        val lookDirection = player.eyeLocation.direction
        val spawnLocation = eyePosition.add(lookDirection)
        spawnLocation.world?.spawnEntity(spawnLocation, EntityType.FIREBALL)
    }

    private var fireBallTask: BukkitTask? = null

    private fun ensureFireballTaskIsActive() {
        if (fireBallTask == null) {
            this.fireBallTask = FireballTask().runTaskTimer(this.main, 0, 1L)
        }
    }

    private class FireballTask : BukkitRunnable() {
        override fun run() {
            Bukkit.getWorlds().forEach { world ->
                world
                    .entities
                    .filter { entity -> entity.type == EntityType.FIREBALL || entity.type == EntityType.SMALL_FIREBALL }
                    .forEach { fireball ->
                        run iteration@{
                            // 3d radius matrix
                            for (x in -FIREBALL_MELT_EFFECT_RADIUS..FIREBALL_MELT_EFFECT_RADIUS) {
                                for (y in -FIREBALL_MELT_EFFECT_RADIUS..FIREBALL_MELT_EFFECT_RADIUS) {
                                    for (z in -FIREBALL_MELT_EFFECT_RADIUS..FIREBALL_MELT_EFFECT_RADIUS) {
                                        // scanned block position
                                        val position = fireball.location.add(x.toDouble(), y.toDouble(), z.toDouble())

                                        // if the scanned block is in the sphere of radius FIREBALL_MELT_EFFECT_RADIUS
                                        if (position.distanceSquared(fireball.location) <= NumberConversions.square(FIREBALL_MELT_EFFECT_RADIUS.toDouble())) {
                                            if (FIREBALL_MELT_AFFECTED_BLOCKS.contains(position.block.type)) {
                                                position.block.setType(Material.AIR, false)
                                            }
                                        }
                                    }
                                }
                            }
                            // SMTH
                        }
                    }
            }
        }
    }
}
