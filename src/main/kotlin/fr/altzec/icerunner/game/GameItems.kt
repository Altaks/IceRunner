package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object GameItems {


    // ---------------- WAITING PHASE ---------------- //
    val redTeamTag: ItemStack = ItemFactory(Material.RED_DYE, 1).setDisplayName("${ChatColor.RED}\u00BB Equipe rouge \u00AB").build()
    val blueTeamTag: ItemStack = ItemFactory(Material.BLUE_DYE, 1).setDisplayName("${ChatColor.BLUE}\u00BB Equipe bleue \u00AB").build()

    private const val PLAYER_INVENTORY_HOTBAR_RED_TEAM_SLOT_INDEX = 3
    private const val PLAYER_INVENTORY_HOTBAR_WAITING_HAND_SLOT_INDEX = 4
    private const val PLAYER_INVENTORY_HOTBAR_BLUE_TEAM_SLOT_INDEX = 5

    fun applyWaitingInventoryToPlayer(player: Player) {
        player.inventory.clear()
        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_RED_TEAM_SLOT_INDEX, redTeamTag);
        player.inventory.setItem(PLAYER_INVENTORY_HOTBAR_BLUE_TEAM_SLOT_INDEX, blueTeamTag);
        player.inventory.heldItemSlot = PLAYER_INVENTORY_HOTBAR_WAITING_HAND_SLOT_INDEX
    }
}