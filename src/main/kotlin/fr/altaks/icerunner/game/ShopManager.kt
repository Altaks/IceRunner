package fr.altaks.icerunner.game

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.shopitems.ShopArrows
import fr.altaks.icerunner.game.shopitems.ShopDistorsionPearls
import fr.altaks.icerunner.game.shopitems.ShopExplosiveSheep
import fr.altaks.icerunner.game.shopitems.ShopFireball
import fr.altaks.icerunner.game.shopitems.ShopIgloo
import fr.altaks.icerunner.game.shopitems.ShopIronMan
import fr.altaks.icerunner.game.shopitems.ShopKamikaze
import fr.altaks.icerunner.game.shopitems.ShopLastJudgement
import fr.altaks.icerunner.game.shopitems.ShopReinforcedBridge
import fr.altaks.icerunner.utils.ItemComparator
import fr.mrmicky.fastinv.FastInv
import fr.mrmicky.fastinv.FastInvManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class ShopManager(val main: Main) : Listener {

    companion object {
        interface IShopItem : Listener {
            fun cost(): UInt
            fun item(): ItemStack
            fun position(): Int
        }

        private const val INVENTORY_LINE_LENGTH = 9
        private const val SHOP_SIZE = 1 * INVENTORY_LINE_LENGTH; // 9 slots
        private val SHOP_NAME = "${ChatColor.DARK_GRAY}⛁ Boutique"

        private val SHOP_SYMBOL_DEFAULT = GameItems.shopSymbolItem(0u)
        private val MAX_PLAYER_MONEY = Material.GOLD_NUGGET.maxStackSize.toUInt()
    }

    private class ShopInventory : FastInv {
        val main: Main
        val itemToShopItemInstanceMapping = HashMap<ItemStack, IShopItem>()

        constructor(main: Main) : super(SHOP_SIZE, SHOP_NAME) {
            this.main = main

            listOf(
                ShopArrows(),
                ShopDistorsionPearls(),
                ShopReinforcedBridge(main),
                ShopKamikaze(main),
                ShopFireball(main),
                ShopIronMan(),
                ShopExplosiveSheep(main),
                ShopIgloo(),
                ShopLastJudgement(),
            ).forEach { shopItem ->
                run {
                    setItem(shopItem.position(), shopItem.item())
                    itemToShopItemInstanceMapping[shopItem.item()] = shopItem
                    Bukkit.getPluginManager().registerEvents(shopItem, main)
                }
            }
        }

        override fun onClick(event: InventoryClickEvent) {
            event.isCancelled = true

            val shopItem = itemToShopItemInstanceMapping[event.currentItem] ?: return
            val player = event.whoClicked as Player

            if (this.main.shopManager.hasPlayerEnoughMoney(player, shopItem.cost())) {
                this.main.shopManager.reducePlayerMoney(player, shopItem.cost())
                player.inventory.addItem(shopItem.item())
            } else {
                event.whoClicked.sendMessage(
                    "${Main.MAIN_PREFIX}${ChatColor.LIGHT_PURPLE} Vous n'avez pas l'argent nécessaire pour acheter cet objet. " +
                        "Il vous manque : ${ChatColor.GOLD}${shopItem.cost() - this.main.shopManager.getPlayerMoney(player)} ⛁",
                )
            }
        }
    }

    private var globalShopInventory: ShopInventory? = null
    private val playerToMoneyMapping = HashMap<Player, UInt>()

    fun initShop() {
        FastInvManager.register(this.main)
    }

    fun preparePlayerShops() {
        Bukkit.getOnlinePlayers().forEach { playerToMoneyMapping[it] = 0u }
    }

    fun getPlayerMoney(player: Player): UInt = playerToMoneyMapping[player] ?: throw IllegalStateException("The player ${player.displayName} is not registered to have any money")

    fun hasPlayerEnoughMoney(player: Player, checkedQuantity: UInt): Boolean = getPlayerMoney(player) >= checkedQuantity

    fun addPlayerMoney(player: Player, quantityToReduce: UInt) {
        playerToMoneyMapping[player] = min(MAX_PLAYER_MONEY, getPlayerMoney(player) + quantityToReduce)
        GameItems.updateShopSymbolInPlayerInventory(player, getPlayerMoney(player))
    }

    fun reducePlayerMoney(player: Player, quantityToReduce: UInt) {
        playerToMoneyMapping[player] = getPlayerMoney(player) - quantityToReduce
        GameItems.updateShopSymbolInPlayerInventory(player, getPlayerMoney(player))
    }

    fun setPlayerMoney(player: Player, amount: UInt) {
        playerToMoneyMapping[player] = amount
        GameItems.updateShopSymbolInPlayerInventory(player, getPlayerMoney(player))
    }

    @EventHandler
    fun onPlayerOpensShop(event: PlayerInteractEvent) {
        if (event.hasItem() && ItemComparator.compare(event.item!!, SHOP_SYMBOL_DEFAULT)) {
            if (this.globalShopInventory == null) {
                this.globalShopInventory = ShopInventory(this.main)
            }
            this.globalShopInventory?.open(event.player)
        }
    }
}
