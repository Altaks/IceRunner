package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemComparator
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlotGroup
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
        .addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE,AttributeModifier(Attribute.KNOCKBACK_RESISTANCE.keyOrThrow, 1.0, AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.CHEST))
        .setUnbreakable(true)
        .build()

    override fun position(): Int = 5

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        if (event.item != null && ItemComparator.compare(event.item, item()) && event.action != Action.PHYSICAL) {
            event.player.inventory.chestplate = item()
            event.player.inventory.setItem(event.hand!!, null)
        }
    }

}
