package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopDistorsionPearls : ShopManager.Companion.IShopItem {
    override fun cost(): UInt = 3u
    override fun item(): ItemStack = ItemFactory(Material.ENDER_PEARL, 2)
        .setDisplayName("${ChatColor.LIGHT_PURPLE}\uD83C\uDF0C Perle de distortion")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Perle cristalline du Jötunheim, forgée par les géants de",
            "${ChatColor.GRAY}glace. Lancer cette perle te téléportera à l'endroit de",
            "${ChatColor.GRAY}ton choix, comme un éclair fendant l'air glacial",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 1
}
