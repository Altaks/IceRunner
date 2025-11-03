package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopKamikaze : ShopManager.Companion.IShopItem {
    override fun cost(): UInt = 3u
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
}
