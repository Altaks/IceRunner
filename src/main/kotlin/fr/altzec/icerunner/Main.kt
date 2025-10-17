package fr.altzec.fr.altzec.icerunner

import fr.altzec.fr.altzec.icerunner.game.GameManager
import fr.altzec.fr.altzec.icerunner.triggers.commands.DevCommand
import fr.altzec.fr.altzec.icerunner.triggers.listeners.DevListener
import fr.altzec.fr.altzec.icerunner.triggers.listeners.PlayerJoinQuitListener
import fr.altzec.fr.altzec.icerunner.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        val MAIN_PREFIX: String = "${ChatColor.GRAY}[${ChatColor.BLUE}IceRunner${ChatColor.GRAY}] \u00BB${ChatColor.RESET}"
    }

    val gameManager: GameManager = GameManager(this)
    val worldManager: WorldManager = WorldManager(this)
    val pluginLogger = Bukkit.getLogger()

    override fun onEnable() {
        super.onEnable()

        registerCommand("dev", DevCommand(this))

        Bukkit.getPluginManager().registerEvents(PlayerJoinQuitListener(this), this)
        Bukkit.getPluginManager().registerEvents(DevListener(), this)
    }

    override fun onDisable() {
        super.onDisable()
    }

    /**
     * Registers a command and assigns it a TabCompleter and a CommandExecutor, thus a TabExecutor
     */
    fun registerCommand(command: String, tabExecutor: TabExecutor) {
        val command: PluginCommand? = getCommand(command)
        command?.setExecutor(tabExecutor)
        command?.tabCompleter = tabExecutor
        pluginLogger.info("Command ${command?.usage} has been registered")
    }
}
