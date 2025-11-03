package fr.altaks.icerunner.game

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.utils.ItemComparator
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import java.util.Objects
import java.util.stream.Stream

@Disabled("Disabled because mockbukkit cannot handle NMS related reflection (caused by the scoreboard implementation)")
open class GameItemsTests {

    companion object {
        @JvmStatic
        fun provideTeamColors(): Stream<Color?> = Stream.of(Color.RED, Color.BLUE)
    }

    private lateinit var server: ServerMock
    private lateinit var plugin: Main

    @BeforeEach
    fun setUp() {
        // Start the mock server
        server = MockBukkit.mock()

        // Load the plugin
        plugin = MockBukkit.load(Main::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun testApplyWaitingInventoryToPlayer() {
        val player = server.addPlayer()
        GameItems.applyWaitingInventoryToPlayer(player)

        assert(!player.inventory.isEmpty) { "Player inventory must be not empty" }
        assert(!Objects.isNull(player.inventory.getItem(3))) { "Player's 3 indexed item must not be null" }
        assert(ItemComparator.compare(player.inventory.getItem(3), GameItems.redTeamTag)) { "Player 3 indexed item must be the red team tag" }

        assert(!Objects.isNull(player.inventory.getItem(5))) { "Player's 5 indexed item must not be null" }
        assert(ItemComparator.compare(player.inventory.getItem(5), GameItems.blueTeamTag)) { "Player 3 indexed item must be the blue team tag" }

        assert(player.inventory.heldItemSlot == 4) { "Player held item slot must be 4 (center of the hotbar)" }
    }

    @ParameterizedTest
    @MethodSource("provideTeamColors")
    fun testApplyTeamPlayingInventoryToPlayer(color: Color) {
        val player = server.addPlayer()
        GameItems.applyPlayingInventoryToPlayer(player, color, 50u)

        assert(!player.inventory.isEmpty) { "Player inventory must be not empty" }

        val zeroIndexedItem = player.inventory.getItem(0)
        assert(!Objects.isNull(zeroIndexedItem)) { "Player's 0 indexed item must not be null" }
        assert(zeroIndexedItem?.type == Material.BOW) { "Player 0 indexed item must be a bow" }
        assert(zeroIndexedItem?.amount == 1) { "Player 0 indexed item must be one single bow" }
        assert(zeroIndexedItem?.itemMeta?.hasDisplayName() ?: false) { "Player 0 indexed item must have display name" }
        assert(zeroIndexedItem?.itemMeta?.displayName().toString().contains("Arc elfique")) { "Player 0 indexed item must have the right display name" }
        assert(zeroIndexedItem?.itemMeta?.hasEnchant(Enchantment.POWER) ?: false) { "Player 0 indexed item must have the POWER enchantment" }
        assert(zeroIndexedItem?.itemMeta?.hasEnchant(Enchantment.KNOCKBACK) ?: false) { "Player 0 indexed item must have the KNOCKBACK enchantment" }
        assert(zeroIndexedItem?.itemMeta?.hasLore() ?: false) { "Player 0 indexed item must have a lore" }
        assert(zeroIndexedItem?.itemMeta?.isUnbreakable ?: false) { "Player 0 indexed item must be unbreakable" }
        assert(zeroIndexedItem?.itemMeta?.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) ?: false) { "Player 0 indexed item must have the HIDE_UNBREAKABLE flag" }
        assert(ItemComparator.compare(zeroIndexedItem, GameItems.baseKitBow)) { "Player 0 indexed item match be the bow item" }

        val oneIndexedItem = player.inventory.getItem(1)
        assert(!Objects.isNull(oneIndexedItem)) { "Player's 1 indexed item must not be null" }
        assert(oneIndexedItem?.itemMeta?.hasDisplayName() ?: false) { "Player 1 indexed item must have display name" }
        assert(oneIndexedItem?.itemMeta?.displayName().toString().contains("Bifrost")) { "Player 1 indexed item must have the right display name" }
        assert(oneIndexedItem?.itemMeta?.hasLore() ?: false) { "Player 1 indexed item must have the lore item" }
        assert(ItemComparator.compare(oneIndexedItem, GameItems.baseKitSnowballs)) { "Player 1 indexed item must be snowballs" }

        val sevenIndexedItem = player.inventory.getItem(7)
        assert(!Objects.isNull(sevenIndexedItem)) { "Player's 7 indexed item must not be null" }
        assert(sevenIndexedItem?.itemMeta?.hasDisplayName() ?: false) { "Player 7 indexed item must have display name" }
        assert(sevenIndexedItem?.itemMeta?.displayName().toString().contains("Flèche explosive")) { "Player 7 indexed item must have the right display name" }
        assert(sevenIndexedItem?.itemMeta?.hasLore() ?: false) { "Player 7 indexed item must have the lore item" }
        assert(ItemComparator.compare(sevenIndexedItem, GameItems.baseKitArrows)) { "Player 7 indexed item must be arrows" }

        assert(!Objects.isNull(player.inventory.helmet)) { "Player's helmet must be not null" }
        assert(player.inventory.helmet?.itemMeta?.isUnbreakable ?: false) { "Player's helmet must be unbreakable" }
        assert(player.inventory.helmet?.itemMeta is LeatherArmorMeta) { "Player's helmet must be a leather armor" }
        assert((player.inventory.helmet?.itemMeta as LeatherArmorMeta).color == color) { "Player leather helmet must be colored with the specified color" }

        assert(!Objects.isNull(player.inventory.chestplate)) { "Player's chestplate must be not null" }
        assert(player.inventory.chestplate?.itemMeta?.isUnbreakable ?: false) { "Player's chestplate must be unbreakable" }
        assert(player.inventory.chestplate?.itemMeta is LeatherArmorMeta) { "Player's chestplate must be a leather armor" }
        assert((player.inventory.chestplate?.itemMeta as LeatherArmorMeta).color == color) { "Player leather chestplate must be colored with the specified color" }

        assert(!Objects.isNull(player.inventory.leggings)) { "Player's leggings must be not null" }
        assert(player.inventory.leggings?.itemMeta?.isUnbreakable ?: false) { "Player's leggings must be unbreakable" }
        assert(player.inventory.leggings?.itemMeta is LeatherArmorMeta) { "Player's leggings must be a leather armor" }
        assert((player.inventory.leggings?.itemMeta as LeatherArmorMeta).color == color) { "Player leather leggings must be colored with the specified color" }

        assert(!Objects.isNull(player.inventory.boots)) { "Player's boots must be not null" }
        assert(player.inventory.boots?.itemMeta?.isUnbreakable ?: false) { "Player's boots must be unbreakable" }
        assert(player.inventory.boots?.itemMeta is LeatherArmorMeta) { "Player's boots must be a leather armor" }
        assert((player.inventory.boots?.itemMeta as LeatherArmorMeta).color == color) { "Player leather boots must be colored with the specified color" }
    }
}
