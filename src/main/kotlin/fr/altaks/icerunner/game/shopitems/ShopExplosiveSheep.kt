package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopExplosiveSheep : ShopManager.Companion.IShopItem {

    override fun cost(): UInt = 16u
    override fun item(): ItemStack = ItemFactory(Material.SHEEP_SPAWN_EGG, 1)
        .setDisplayName("${ChatColor.RED}☣ File un mauvais coton")
        .setLore(
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GRAY}Né des vents hurlants de Niflheim, ce mouton maudit bondit",
            "${ChatColor.GRAY}vers son destin dans un cri tonitruant. Guidé par ton",
            "${ChatColor.GRAY}regard, il charge l’ennemi avant d’exploser dans une",
            "${ChatColor.GRAY}lumière glacée et divine.",
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()

    override fun position(): Int = 6
}
