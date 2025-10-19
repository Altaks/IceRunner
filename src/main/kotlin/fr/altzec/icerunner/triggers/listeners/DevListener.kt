package fr.altzec.fr.altzec.icerunner.triggers.listeners

import fr.altzec.fr.altzec.icerunner.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class DevListener(val main: Main) : Listener {

    @EventHandler
    fun onPlayerClicksEvent(event: PlayerInteractEvent) {
        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK) {
            val loc = event.clickedBlock?.location!!
            main.logger.warning("x:${loc.blockX},y:${loc.blockY},z:${loc.blockZ}")
        }
    }
}
