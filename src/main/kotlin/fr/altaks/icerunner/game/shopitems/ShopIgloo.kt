package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopIgloo : ShopManager.Companion.IShopItem {

    override fun cost(): UInt = 12u
    override fun item(): ItemStack = ItemFactory(Material.BEACON, 1)
        .setDisplayName("${ChatColor.AQUA}\uD83C\uDF05 Igloo")
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
}
