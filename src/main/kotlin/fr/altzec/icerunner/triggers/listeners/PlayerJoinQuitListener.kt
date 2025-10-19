package fr.altzec.fr.altzec.icerunner.triggers.listeners

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.game.GameItems
import fr.altzec.fr.altzec.icerunner.game.TeamsManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerJoinQuitListener(val main: Main) : Listener {

    companion object {
        private val PLAYER_LIST_HEADER: String =
            "${ChatColor.GRAY}[${ChatColor.AQUA}Ice Runner${ChatColor.GRAY}] \n" +
                "${ChatColor.GRAY}Made by ${ChatColor.LIGHT_PURPLE}Altaks ${ChatColor.GRAY}& ${ChatColor.LIGHT_PURPLE}_Zecross_" +
                "\n"

        private val PLAYER_LIST_FOOTER: String =
            "\n" +
                "${ChatColor.GRAY}\u00BB ${ChatColor.AQUA}https://github.com/Altaks/IceRunner ${ChatColor.GRAY}\u00AB\n" +
                "${ChatColor.LIGHT_PURPLE}altakxs ${ChatColor.GRAY}& ${ChatColor.LIGHT_PURPLE}zecross256 ${ChatColor.GRAY}on ${ChatColor.BLUE}Discord"
    }

    @EventHandler
    fun onPlayerJoinsServer(event: PlayerJoinEvent) {
        event.joinMessage = "${ChatColor.GRAY}[${ChatColor.GREEN}+${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"
        event.player.setPlayerListHeaderFooter(PLAYER_LIST_HEADER, PLAYER_LIST_FOOTER)

        if (!this.main.gameManager.hasGameStarted() && !this.main.gameManager.isGameStarting()) {
            GameItems.applyWaitingInventoryToPlayer(event.player)

            if (Bukkit.getOnlinePlayers().size >= TeamsManager.PLAYERS_REQUIRED_TO_START_GAME) {
                this.main.gameManager.triggerStartingGamePhase()
            } else {
                Bukkit.broadcastMessage("${Main.MAIN_PREFIX} Il manque ${TeamsManager.PLAYERS_REQUIRED_TO_START_GAME - Bukkit.getOnlinePlayers().size} joueurs pour débuter la partie !")
            }
        }
    }

    @EventHandler
    fun onPlayerQuitsServer(event: PlayerQuitEvent) {
        event.quitMessage = "${ChatColor.GRAY}[${ChatColor.RED}-${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"

        if (this.main.gameManager.isGameStarting()) {
            throw NotImplementedError("Fallback to waiting mode is not implemented yet !")
        }
    }
}
