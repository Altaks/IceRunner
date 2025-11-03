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
            "${ChatColor.GRAY}La pointe de ces flèches est remplie d’un liquide rougeoyant,",
            "${ChatColor.GRAY}extrait des glandes venimeuses des dragons des marais. ",
            "${ChatColor.GRAY}À l’impact, elles explosent en une gerbe de flammes bleutées,",
            "${ChatColor.GRAY}laissant derrière elles une odeur de soufre et de cendre.",
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
