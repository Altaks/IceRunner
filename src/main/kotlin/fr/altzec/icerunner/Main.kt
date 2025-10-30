package fr.altzec.fr.altzec.icerunner

import fr.altzec.fr.altzec.icerunner.game.GameManager
import fr.altzec.fr.altzec.icerunner.game.ScoreboardManager
import fr.altzec.fr.altzec.icerunner.game.TeamsManager
import fr.altzec.fr.altzec.icerunner.triggers.commands.DevCommand
import fr.altzec.fr.altzec.icerunner.triggers.listeners.DevListener
import fr.altzec.fr.altzec.icerunner.triggers.listeners.PlayerJoinQuitListener
import fr.altzec.fr.altzec.icerunner.utils.TextGradientUtils
import fr.altzec.fr.altzec.icerunner.world.WorldManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

open class Main : JavaPlugin {

    constructor() : super()

    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File?) : super(loader, description, dataFolder, file ?: File(""))

    companion object {
        val GAME_NAME = "${ChatColor.GRAY}[${TextGradientUtils.generateGradient("IceRunner", "#59B1FD", "#59FDF8")}${ChatColor.GRAY}]${ChatColor.RESET}"
        val MAIN_PREFIX: String = "$GAME_NAME ${ChatColor.GRAY}\u00BB${ChatColor.RESET}"
    }

    val pluginLogger = Bukkit.getLogger()

    val gameManager: GameManager = GameManager(this)
    val worldManager: WorldManager = WorldManager(this)
    val teamsManager: TeamsManager = TeamsManager(this)
    val scoreboardManager: ScoreboardManager = ScoreboardManager(this)

    override fun onEnable() {
        super.onEnable()
        saveDefaultConfig()

        this.teamsManager.prepareTeams()
        this.scoreboardManager.initScoreboardUpdating()

        registerCommand("dev", DevCommand(this))

        // Register event listeners
        listOf(
            PlayerJoinQuitListener(this),
            DevListener(this),
            teamsManager,
            scoreboardManager,
        ).forEach { listener -> Bukkit.getPluginManager().registerEvents(listener, this) }

        Bukkit.getOnlinePlayers().forEach { player -> this.scoreboardManager.initPlayerScoreboard(player) }
    }

    override fun onDisable() {
        super.onDisable()

        Bukkit.getOnlinePlayers().forEach { player -> this.scoreboardManager.unloadPlayerScoreboard(player) }
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
