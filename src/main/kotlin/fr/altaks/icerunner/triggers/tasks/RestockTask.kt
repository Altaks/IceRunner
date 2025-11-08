package fr.altaks.icerunner.triggers.tasks

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.utils.ItemComparator
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class RestockTask(val main: Main) : BukkitRunnable() {

    companion object {
        private val ITEMS_TO_RESTOCK = listOf(GameItems.baseKitSnowballs, GameItems.baseKitArrows)
    }

    override fun run() {
        if(main.gameManager.isGamePlaying()) {
            Bukkit.getOnlinePlayers().forEach { player -> run {
                ITEMS_TO_RESTOCK.forEach { item -> run {
                    val amountOfSaidItem = player.inventory.filterNotNull().filter { ItemComparator.compare(item, it) }.sumOf { it.amount }
                    if(amountOfSaidItem < item.amount) {
                        val toRestockItemStack = item.clone()
                        toRestockItemStack.amount = 1
                        player.inventory.addItem(toRestockItemStack)
                    }
                } }
            } }
        }
    }
}