package fr.altzec.fr.altzec.icerunner.triggers.listeners

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.game.GameItems
import fr.altzec.fr.altzec.icerunner.game.GameManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerJoinQuitListener(val main: Main) : Listener {

    @EventHandler
    fun onPlayerJoinsServer(event: PlayerJoinEvent) {
        event.joinMessage = "${ChatColor.GRAY}[${ChatColor.GREEN}+${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"

        if (!this.main.gameManager.hasGameStarted()) {
            if (Bukkit.getOnlinePlayers().size >= GameManager.PLAYERS_REQUIRED_TO_START_GAME) {
                this.main.gameManager.startGame()
            } else {
                GameItems.applyWaitingInventoryToPlayer(event.player)
                Bukkit.broadcastMessage("${Main.MAIN_PREFIX} Il manque ${GameManager.PLAYERS_REQUIRED_TO_START_GAME - Bukkit.getOnlinePlayers().size} joueurs pour débuter la partie !")
            }
        }
    }

    @EventHandler
    fun onPlayerQuitsServer(event: PlayerQuitEvent) {
        event.quitMessage = "${ChatColor.GRAY}[${ChatColor.RED}-${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"
    }
}
