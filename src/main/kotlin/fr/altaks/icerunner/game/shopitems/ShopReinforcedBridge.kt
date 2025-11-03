package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopReinforcedBridge : ShopManager.Companion.IShopItem {
    override fun cost(): UInt = 5u
    override fun item(): ItemStack = ItemFactory(Material.PHANTOM_MEMBRANE, 5)
        .setDisplayName("${ChatColor.BLUE}❄ Eternuement de Yéti")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Artéfact maudit de neige tombée des montagnes de Jotunheim.",
            "${ChatColor.GRAY}En utilisant ces cristaux, un pont de glace solide se forme,",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 2
}
