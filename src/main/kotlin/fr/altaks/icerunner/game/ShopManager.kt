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
import java.util.UUID
import kotlin.collections.forEach
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

        private val UNMOVABLE_MATERIALS = listOf(Material.BOW, Material.GOLD_NUGGET)
    }

    val itemToShopItemInstanceMapping = HashMap<Material, IShopItem>()

    private class ShopInventory : FastInv {
        val main: Main
        val shopManager: ShopManager

        constructor(main: Main, shopManager: ShopManager) : super(SHOP_SIZE, SHOP_NAME) {
            this.main = main
            this.shopManager = shopManager

            listOf(
                ShopArrows(),
                ShopDistorsionPearls(),
                ShopReinforcedBridge(main),
                ShopKamikaze(main),
                ShopFireball(main),
                ShopIronMan(),
                ShopExplosiveSheep(main),
                ShopIgloo(),
                ShopLastJudgement(main),
            ).forEach { handler ->
                run {
                    setItem(handler.position(), handler.item())
                    shopManager.itemToShopItemInstanceMapping[handler.item().type] = handler
                    Bukkit.getPluginManager().registerEvents(handler, main)
                }
            }
        }

        override fun onClick(event: InventoryClickEvent) {
            event.isCancelled = true

            val shopItem = shopManager.itemToShopItemInstanceMapping[event.currentItem?.type] ?: return
            val player = event.whoClicked as Player

            if (shopManager.hasPlayerEnoughMoney(player, shopItem.cost())) {
                shopManager.reducePlayerMoney(player, shopItem.cost())
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
    private val playerToMoneyMapping = HashMap<UUID, UInt>()

    fun initShop() {
        FastInvManager.register(this.main)
    }

    fun preparePlayerShops() {
        Bukkit.getOnlinePlayers().forEach { playerToMoneyMapping[it.uniqueId] = 0u }
    }

    fun getPlayerMoney(player: Player): UInt = playerToMoneyMapping[player.uniqueId] ?: 0u

    fun hasPlayerEnoughMoney(player: Player, checkedQuantity: UInt): Boolean = getPlayerMoney(player) >= checkedQuantity

    fun addPlayerMoney(player: Player, quantityToReduce: UInt) {
        playerToMoneyMapping[player.uniqueId] = min(MAX_PLAYER_MONEY, getPlayerMoney(player) + quantityToReduce)
        GameItems.updateShopSymbolInPlayerInventory(player, getPlayerMoney(player))
    }

    fun reducePlayerMoney(player: Player, quantityToReduce: UInt) {
        playerToMoneyMapping[player.uniqueId] = getPlayerMoney(player) - quantityToReduce
        GameItems.updateShopSymbolInPlayerInventory(player, getPlayerMoney(player))
    }

    fun setPlayerMoney(player: Player, amount: UInt) {
        playerToMoneyMapping[player.uniqueId] = amount
        GameItems.updateShopSymbolInPlayerInventory(player, getPlayerMoney(player))
    }

    @EventHandler
    fun onPlayerOpensShop(event: PlayerInteractEvent) {
        if (event.hasItem() && ItemComparator.compare(event.item!!, SHOP_SYMBOL_DEFAULT)) {
            if (this.globalShopInventory == null) {
                this.globalShopInventory = ShopInventory(this.main, this)
            }
            this.globalShopInventory?.open(event.player)
        }
    }

    fun resetPlayerLastJudgementTaskIfActive(player: Player) {
        this.itemToShopItemInstanceMapping.filter { (_, handler) -> handler is ShopLastJudgement }.forEach { (_, handler) -> (handler as ShopLastJudgement).cancelLastJudgement(player) }
    }

    @EventHandler
    fun onPlayerMovesItemInInv(event: InventoryClickEvent) {
        if (event.currentItem != null) {
            if (ItemComparator.compare(GameItems.baseKitBow, event.currentItem) || ItemComparator.compare(SHOP_SYMBOL_DEFAULT, event.currentItem)) {
                event.isCancelled = true
            }
        } else if (event.cursor != null) {
            if (ItemComparator.compare(GameItems.baseKitBow, event.cursor) || ItemComparator.compare(SHOP_SYMBOL_DEFAULT, event.cursor)) {
                event.isCancelled = true
            }
        }
    }
}
