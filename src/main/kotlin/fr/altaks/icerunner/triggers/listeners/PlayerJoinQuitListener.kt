package fr.altaks.icerunner.triggers.listeners

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.utils.TextGradientUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerJoinQuitListener(val main: Main) : Listener {

    companion object {
        private val PLAYER_LIST_HEADER: String =
            "${Main.GAME_NAME} \n" +
                "${ChatColor.GRAY}Made by ${TextGradientUtils.generateGradient("Altaks", "#CA02AE", "#8102CA")}" +
                "\n"

        private val PLAYER_LIST_FOOTER: String =
            "\n" +
                "${ChatColor.GRAY}\u00BB ${ChatColor.AQUA}https://github.com/Altaks/IceRunner ${ChatColor.GRAY}\u00AB\n" +
                "${TextGradientUtils.generateGradient("altakxs", "#CA02AE", "#8102CA")}${ChatColor.GRAY} on ${ChatColor.BLUE}Discord"
    }

    @EventHandler
    fun onPlayerJoinsServer(event: PlayerJoinEvent) {
        event.joinMessage = "${ChatColor.GRAY}[${ChatColor.GREEN}+${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"
        event.player.setPlayerListHeaderFooter(PLAYER_LIST_HEADER, PLAYER_LIST_FOOTER)

        if (!this.main.gameManager.hasGameStarted()) {
            GameItems.applyWaitingInventoryToPlayer(event.player)
            event.player.health = 20.0
            event.player.teleport(this.main.worldManager.loadedWorldMetadata?.mapCenterCoordinates?.clone()?.add(0.0, 1.5, 0.0) ?: throw IllegalStateException("Unable to acquire map center coordinates"))
            this.main.gameManager.tryToStartGame()
        } else if(this.main.teamsManager.playerHasGameTeam(event.player)){
            // Teleport back to team spawn
            val team = this.main.teamsManager.getPlayerGameTeam(event.player)
            val respawnPoint = team.respawnPoint(this.main.worldManager.loadedWorldMetadata ?: throw IllegalStateException("The loaded world variant metadata should exist"))
            event.player.teleport(respawnPoint)
        } else {
            event.player.gameMode = GameMode.SPECTATOR
        }
    }

    @EventHandler
    fun onPlayerQuitsServer(event: PlayerQuitEvent) {
        event.quitMessage = "${ChatColor.GRAY}[${ChatColor.RED}-${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"
    }
}
