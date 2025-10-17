package fr.altzec.fr.altzec.icerunner.triggers.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class DevListener : Listener {

    @EventHandler
    fun onPlayerClicksEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            println(event.clickedBlock?.location.toString())
        }
    }
}