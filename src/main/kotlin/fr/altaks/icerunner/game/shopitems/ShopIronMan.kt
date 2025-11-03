package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopIronMan : ShopManager.Companion.IShopItem {
    override fun cost(): UInt = 8u
    override fun item(): ItemStack = ItemFactory(Material.IRON_CHESTPLATE, 1)
        .setDisplayName("${ChatColor.GRAY}\uD83D\uDEE1 Iron man")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Armure forgée dans les foyers d'Asgard, infusée de la",
            "${ChatColor.GRAY}puissance d'Ymir lui-même. Ce plastron te protège des",
            "${ChatColor.GRAY}coups violents, te maintenant ferme comme un iceberg,",
            "${ChatColor.GRAY}défiant les attaques ennemies.",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 5
}
