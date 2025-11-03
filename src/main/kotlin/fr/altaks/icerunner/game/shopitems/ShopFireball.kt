package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopFireball : ShopManager.Companion.IShopItem {
    override fun cost(): UInt = 4u
    override fun item(): ItemStack = ItemFactory(Material.FIRE_CHARGE, 1)
        .setDisplayName("${ChatColor.RED}\uD83D\uDD25 Souffle de flamme")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Essence enflammée du dragon Nidhögg, soufflée des abîmes. ",
            "${ChatColor.GRAY}Cette boule de feu, aux flammes ardentes, incinère tout sur",
            "${ChatColor.GRAY}son passage, défiant la glace, le vent, et l'adversité",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 4
}
