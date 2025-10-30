package fr.altzec.fr.altzec.icerunner.triggers.listeners

import fr.altzec.fr.altzec.icerunner.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPingListener(val main: Main) : Listener {

    @EventHandler
    fun onServerPingEvent(event: ServerListPingEvent) {
        if(main.serverCachedIcon != null) {
            event.setServerIcon(main.serverCachedIcon)
        }
    }
}