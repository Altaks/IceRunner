package fr.altaks.icerunner

import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.GameManager
import fr.altaks.icerunner.game.ScoreboardManager
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.game.TeamsManager
import fr.altaks.icerunner.triggers.commands.AllChatCommand
import fr.altaks.icerunner.triggers.commands.DevCommand
import fr.altaks.icerunner.triggers.listeners.ArrowListener
import fr.altaks.icerunner.triggers.listeners.DevListener
import fr.altaks.icerunner.triggers.listeners.PlayerJoinQuitListener
import fr.altaks.icerunner.triggers.listeners.ServerListPingListener
import fr.altaks.icerunner.utils.FileUtils
import fr.altaks.icerunner.utils.TextGradientUtils
import fr.altaks.icerunner.world.WorldManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.bukkit.util.CachedServerIcon
import java.io.File
import javax.imageio.ImageIO

open class Main : JavaPlugin {

    constructor() : super()

    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File?) : super(loader, description, dataFolder, file ?: File(""))

    companion object {
        val GAME_NAME = "${ChatColor.GRAY}[${TextGradientUtils.generateGradient("IceRunner", "#59B1FD", "#59FDF8")}${ChatColor.GRAY}]${ChatColor.RESET}"
        val MAIN_PREFIX: String = "$GAME_NAME ${ChatColor.GRAY}\u00BB${ChatColor.RESET}"

        private const val INTERNAL_GAME_ICON_PATH = "game_icon.png"
        private const val EXTERNAL_GAME_ICON_PATH = "icon.png"
    }

    val pluginLogger = Bukkit.getLogger()

    val gameManager: GameManager = GameManager(this)
    val worldManager: WorldManager = WorldManager(this)
    val teamsManager: TeamsManager = TeamsManager(this)
    val scoreboardManager: ScoreboardManager = ScoreboardManager(this)
    val shopManager: ShopManager = ShopManager(this)

    var serverCachedIcon: CachedServerIcon? = null

    override fun onEnable() {
        super.onEnable()
        saveDefaultConfig()

        this.worldManager.setupGameWorld()
        this.teamsManager.prepareTeams()

        this.scoreboardManager.initScoreboardUpdating()
        this.shopManager.initShop()

        registerCommand("dev", DevCommand(this))
        registerCommand("all", AllChatCommand(this))

        // Register event listeners
        listOf(
            PlayerJoinQuitListener(this),
            ServerListPingListener(this),
            DevListener(this),
            ArrowListener(this),
            teamsManager,
            scoreboardManager,
            gameManager,
            shopManager,
        ).forEach { listener -> Bukkit.getPluginManager().registerEvents(listener, this) }

        Bukkit.getOnlinePlayers().forEach { player ->
            this.scoreboardManager.initPlayerScoreboard(player)
            GameItems.applyWaitingInventoryToPlayer(player)
        }

        // Server list related setup
        prepareServerMotdAndIcon()

        this.gameManager.tryToStartGame()
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

    fun prepareServerMotdAndIcon() {
        Bukkit.setMotd(
            "               \u00bb ${ChatColor.YELLOW}Bienvenue en ${TextGradientUtils.generateGradient("IceRunner", "#59B1FD", "#59FDF8")}${ChatColor.YELLOW} !${ChatColor.GRAY} \u00ab\n" +
                "      ${ChatColor.YELLOW}Un ${ChatColor.GOLD}KOTH${ChatColor.YELLOW}, des ${TextGradientUtils.generateGradient("glissades", "#FFFFFF", "#59FDF8")}${ChatColor.YELLOW} et des ${TextGradientUtils.generateGradient("explosions", "ED524F", "EDA14F")} ${ChatColor.YELLOW}!",
        )

        val iconImage = FileUtils.readResource(INTERNAL_GAME_ICON_PATH) ?: throw IllegalArgumentException("Couldn't load server icon")
        this.serverCachedIcon = Bukkit.loadServerIcon(ImageIO.read(iconImage))
    }
}
