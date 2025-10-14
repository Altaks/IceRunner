package fr.altzec.fr.altzec.icerunner

import fr.altzec.fr.altzec.icerunner.triggers.listeners.PlayerJoinQuitListener
import lombok.Getter
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    @Getter
    private val pluginLogger = Bukkit.getLogger()

    override fun onEnable() {
        super.onEnable()

        Bukkit.getPluginManager().registerEvents(PlayerJoinQuitListener(), this)
    }

    override fun onDisable() {
        super.onDisable()
    }
}
