package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

object GameItems {

    // ●○
    private val loreDelimitation: String = "${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}${"  ".repeat(40)}";

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

    val baseKitArrows: ItemStack = ItemFactory(Material.ARROW, 4)
        .setDisplayName("${ChatColor.AQUA}\uD83D\uDCA5 Flèche explosive")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}La pointe de ces flèches est remplie d’un liquide rougeoyant,",
            "${ChatColor.GRAY}extrait des glandes venimeuses des dragons des marais. ",
            "${ChatColor.GRAY}À l’impact, elles explosent en une gerbe de flammes bleutées,",
            "${ChatColor.GRAY}laissant derrière elles une odeur de soufre et de cendre."
        )
        .build()

    val baseKitSnowballs: ItemStack = ItemFactory(Material.SNOWBALL, 8)
        .setDisplayName("${ChatColor.LIGHT_PURPLE}☄ Bifrost")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Les Bifrost ne sont pas de simples boules de neige : ",
            "${ChatColor.GRAY}Ce sont des fragments gelés du Pont des Dieux, forgés par les",
            "${ChatColor.GRAY}sorciers elfes des Glaces Éternelles après avoir découvert un",
            "${ChatColor.GRAY}passage secret vers le royaume d’Yggdrasil."
        )
        .build()

    val baseKitBow: ItemStack = ItemFactory(Material.BOW)
        .setDisplayName("${ChatColor.GOLD}\uD83C\uDFF9 Arc elfique")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Son bois, taillé dans le cœur d’un arbre millénaire, murmure",
            "${ChatColor.GRAY}encore les secrets des batailles passées et vibre à proximité",
            "${ChatColor.GRAY}des échos du bifrost"
        )
        .addSafeEnchantment(Enchantment.POWER, 1)
        .addUnsafeEnchantment(Enchantment.KNOCKBACK, 1)
        .setUnbreakable(true)
        .addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        .build()

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
