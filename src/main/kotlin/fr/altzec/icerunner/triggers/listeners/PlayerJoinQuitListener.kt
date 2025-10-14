package fr.altzec.fr.altzec.icerunner.triggers.listeners

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerJoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoinsServer(event: PlayerJoinEvent) {
        event.joinMessage = "${ChatColor.GRAY}[${ChatColor.GREEN}+${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"
    }

    @EventHandler
    fun onPlayerQuitsServer(event: PlayerQuitEvent) {
        event.quitMessage = "${ChatColor.GRAY}[${ChatColor.RED}-${ChatColor.GRAY}] ${ChatColor.GRAY}${event.player.displayName}"
    }
}
