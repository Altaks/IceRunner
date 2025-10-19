package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

object GameItems {

    // ---------------- WAITING PHASE ---------------- //
    val redTeamTag: ItemStack = ItemFactory(Material.RED_DYE, 1).setDisplayName("${ChatColor.RED}\u00BB Equipe rouge \u00AB").build()
    val blueTeamTag: ItemStack = ItemFactory(Material.LIGHT_BLUE_DYE, 1).setDisplayName("${ChatColor.AQUA}\u00BB Equipe bleue \u00AB").build()

    private const val PLAYER_INVENTORY_HOTBAR_RED_TEAM_SLOT_INDEX = 3
    private const val PLAYER_INVENTORY_HOTBAR_WAITING_HAND_SLOT_INDEX = 4
    private const val PLAYER_INVENTORY_HOTBAR_BLUE_TEAM_SLOT_INDEX = 5

    fun applyWaitingInventoryToPlayer(player: Player) {
        player.inventory.clear()
        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_RED_TEAM_SLOT_INDEX, redTeamTag)
        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_BLUE_TEAM_SLOT_INDEX, blueTeamTag)
        player.inventory.heldItemSlot = PLAYER_INVENTORY_HOTBAR_WAITING_HAND_SLOT_INDEX
    }

    // ---------------- PLAYING PHASE ---------------- //

    private const val PLAYER_INVENTORY_HOTBAR_BOW_SLOT_INDEX = 0
    private const val PLAYER_INVENTORY_HOTBAR_SNOWBALLS_SLOT_INDEX = 1
    private const val PLAYER_INVENTORY_HOTBAR_ARROWS_TEAM_SLOT_INDEX = 7

    private val baseKitArrows: ItemStack = ItemFactory(Material.ARROW, 4).setDisplayName("Flèche explosive").build()
    private val baseKitSnowballs: ItemStack = ItemFactory(Material.SNOWBALL, 8).setDisplayName("Bifrost").build()
    private val baseKitBow: ItemStack = ItemFactory(Material.BOW).setDisplayName("Arc elfique").setUnbreakable(true).build()

    private fun getTeamColoredLeatherArmorPiece(material: Material, color: Color): ItemStack {
        assert(
            arrayOf(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS).contains(material),
        ) { "Specified armor material is not leather-based" }

        val item = ItemStack(material, 1)
        val leatherItemMeta: LeatherArmorMeta = item.itemMeta as LeatherArmorMeta

        leatherItemMeta.setColor(color)
        leatherItemMeta.isUnbreakable = true

        item.itemMeta = leatherItemMeta
        return item
    }

    fun applyPlayingInventoryToPlayer(player: Player, teamColor: Color) {
        player.inventory.clear()

        player.inventory.helmet = getTeamColoredLeatherArmorPiece(Material.LEATHER_HELMET, teamColor)
        player.inventory.chestplate = getTeamColoredLeatherArmorPiece(Material.LEATHER_CHESTPLATE, teamColor)
        player.inventory.leggings = getTeamColoredLeatherArmorPiece(Material.LEATHER_LEGGINGS, teamColor)
        player.inventory.boots = getTeamColoredLeatherArmorPiece(Material.LEATHER_BOOTS, teamColor)

        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_BOW_SLOT_INDEX, baseKitBow)
        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_SNOWBALLS_SLOT_INDEX, baseKitSnowballs)
        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_ARROWS_TEAM_SLOT_INDEX, baseKitArrows)
    }
}
