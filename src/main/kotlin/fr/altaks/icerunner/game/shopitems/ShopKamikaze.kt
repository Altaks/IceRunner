package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemComparator
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions

class ShopKamikaze(val main: Main) : ShopManager.Companion.IShopItem {

    companion object {
        private const val EXPLOSION_EFFECT_RADIUS = 10
        private val EXPLOSION_AFFECTED_BLOCKS = listOf<Material>(Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE)
    }

    override fun cost(): UInt = 12u
    override fun item(): ItemStack = ItemFactory(Material.TRIPWIRE_HOOK, 2)
        .setDisplayName("${ChatColor.DARK_RED}\uD83D\uDCA5 Kamikaze")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Artefact interdit d’Helheim, scellant la rage d’un guerrier",
            "${ChatColor.GRAY}déchu. En l’activant, tu libères ton âme dans une onde",
            "${ChatColor.GRAY}destructrice, fauchant tout être proche dans un dernier",
            "${ChatColor.GRAY}éclat de gloire gelée",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 3

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        if (event.item != null && ItemComparator.compare(event.item, item())) {
            event.isCancelled = true
            triggerPlayerExplosion(event.player)
        }
    }

    private fun triggerPlayerExplosion(player: Player) {
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

        player.world.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 50f, 1f)
        player.world.spawnParticle(Particle.EXPLOSION, player.location, 50, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), 0.1)

        player.world
            .getNearbyEntities(player.location, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble())
            .filter { entity -> entity.type == EntityType.PLAYER }
            .forEach { entity -> this.main.gameManager.respawnPlayer(entity as Player) }
    }
}
