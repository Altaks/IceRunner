package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopLastJudgement : ShopManager.Companion.IShopItem {

    override fun cost(): UInt = 30u
    override fun item(): ItemStack = ItemFactory(Material.BLAZE_POWDER, 1)
        .setDisplayName("${ChatColor.GOLD}⚡ Jugement dernier")
        .setLore(
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GRAY}La poudre enflammée du Feu éternel. En la lançant, tu",
            "${ChatColor.GRAY}invoques le jugement des anciens dieux, un cataclysme",
            "${ChatColor.GRAY}déchaînant la fureur du feu céleste, consumant",
            "${ChatColor.GRAY}toute construction et ennemis sur son passage.",
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()

    override fun position(): Int = 8
}
