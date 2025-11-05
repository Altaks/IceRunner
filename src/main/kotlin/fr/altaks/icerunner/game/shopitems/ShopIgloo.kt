package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions
import kotlin.math.abs

class ShopIgloo : ShopManager.Companion.IShopItem {

    companion object {
        private const val IGLOO_RADIUS = 5
        private const val IGLOO_BUILD_ERROR_MARGIN = 4
        private val ITEM_NAME = "${ChatColor.AQUA}\uD83C\uDF05 Igloo"
    }

    override fun cost(): UInt = 12u

    override fun item(): ItemStack = ItemFactory(Material.BEACON, 1)
        .setDisplayName(ITEM_NAME)
        .setLore(
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GRAY}L'Igloo des anciens géants des glaces. En activant cette",
            "${ChatColor.GRAY}balise, tu invoques un abri magique, refuge contre les",
            "${ChatColor.GRAY}tempêtes arctiques et les assauts ennemis, il s'agit",
            "${ChatColor.GRAY}d'un sanctuaire de froid et de sécurité",
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()

    override fun position(): Int = 7

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        // FIXME : Figure out why beacons don't match using `ItemComparator.compare`
        if (event.item != null && event.item!!.hasItemMeta() && event.item!!.itemMeta!!.displayName == ITEM_NAME) {
            event.isCancelled = true
            buildIglooAroundPlayer(event.player)
            GameItems.decrementHeldItemAmount(event.player)
        }
    }

    private fun buildIglooAroundPlayer(player: Player) {
        val drawingRadius = NumberConversions.square(IGLOO_RADIUS.toDouble())

        // 3d radius matrix
        for (x in -IGLOO_RADIUS..IGLOO_RADIUS) {
            for (y in 0..IGLOO_RADIUS) {
                for (z in -IGLOO_RADIUS..IGLOO_RADIUS) {
                    // scanned block position
                    val position = player.location.add(x.toDouble(), y.toDouble(), z.toDouble())

                    // if the scanned block is in the sphere of radius EXPLOSION_RADIUS
                    val drawingRadiusOffset = abs(position.distanceSquared(player.location) - drawingRadius)
                    if (drawingRadiusOffset <= IGLOO_BUILD_ERROR_MARGIN) {
                        // Apply block replacing
                        val newType = when (position.block.type) {
                            Material.AIR -> Material.ICE
                            Material.ICE -> Material.PACKED_ICE
                            Material.PACKED_ICE -> Material.BLUE_ICE
                            else -> continue
                        }

                        position.block.setType(newType, false)
                    }
                }
            }
        }

        for (x in -IGLOO_RADIUS..IGLOO_RADIUS) {
            for (z in -IGLOO_RADIUS..IGLOO_RADIUS) {
                val position = player.location.add(x.toDouble(), -1.0, z.toDouble())
                val distance = position.distanceSquared(player.location)
                if (distance <= drawingRadius) {
                    // Apply block replacing
                    val newType = when (position.block.type) {
                        Material.AIR -> Material.ICE
                        Material.ICE -> Material.PACKED_ICE
                        Material.PACKED_ICE -> Material.BLUE_ICE
                        else -> continue
                    }

                    position.block.setType(newType, false)
                }
            }
        }
    }
}
