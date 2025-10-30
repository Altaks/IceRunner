package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.triggers.tasks.PlayingPhaseTask
import fr.altzec.fr.altzec.icerunner.triggers.tasks.StartingPhaseTask
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitTask

class GameManager(val main: Main) {

    private var gameState: GameState = GameState.WAITING

    private var startingPhaseTask: BukkitTask? = null
    private var playingPhaseTask: BukkitTask? = null

    companion object {
        private val CONGRATS_DECORATION = "${ChatColor.RED}${ChatColor.MAGIC}!${ChatColor.AQUA}${ChatColor.MAGIC}!${ChatColor.GREEN}${ChatColor.MAGIC}!${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}!${ChatColor.GOLD}${ChatColor.MAGIC}!"
        private val CONGRATS_DECORATION_REVERSED = "${ChatColor.GOLD}${ChatColor.MAGIC}!${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}!${ChatColor.GREEN}${ChatColor.MAGIC}!${ChatColor.AQUA}${ChatColor.MAGIC}!${ChatColor.RED}${ChatColor.MAGIC}!"
    }

    fun triggerStartingGamePhase() {
        this.gameState = GameState.STARTING
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX}${ChatColor.LIGHT_PURPLE} La phase de sélection d'équipes commence !")

        this.main.worldManager.setupGameWorld()
        this.main.worldManager.teleportPlayersToGameWorld()

        // Starting and storing the startingPhaseTask
        this.startingPhaseTask = StartingPhaseTask(this.main).runTaskTimer(this.main, 0, 20)
    }

    fun triggerPlayingGamePhase() {
        this.gameState = GameState.PLAYING
        Bukkit.broadcastMessage(
            "${Main.MAIN_PREFIX}${ChatColor.LIGHT_PURPLE} La partie commence !...\n" +
                "${ChatColor.GRAY}Le but du jeu est simple, capturer les différentes îles : \n" +
                "${ChatColor.GRAY}- L'île ${ChatColor.WHITE}blanche${ChatColor.GRAY}, située au centre rapporte le plus de points à votre équipe.\n" +
                "${ChatColor.GRAY}- Les îles secondaires (${ChatColor.GREEN}verte${ChatColor.GRAY} et ${ChatColor.YELLOW}jaune${ChatColor.GRAY}) rapportent aussi des points.\n" +
                "\n\n" +
                "${ChatColor.GRAY}Vous disposez de ${ChatColor.UNDERLINE}boules de neige${ChatColor.RESET}${ChatColor.GRAY} qui forment des ponts de glace pour vous rendrez sur les différents objectifs, mais aussi des ${ChatColor.UNDERLINE}flèches explosives${ChatColor.RESET}${ChatColor.GRAY} pour \u00AB briser la glace \u00BB avec vos adversaires.\n" +
                "${ChatColor.GRAY}Une équipe remporte la partie lorsqu'elle atteint ${ChatColor.UNDERLINE}360 points${ChatColor.RESET}${ChatColor.GRAY}.\n" +
                "${Main.MAIN_PREFIX} ${ChatColor.YELLOW}${ChatColor.GOLD}Bonne partie et bonne chance à tous !\n",
        )

        this.main.teamsManager.teleportPlayersToTheirTeamSpawnAndSetRespawnPoints()
        this.main.teamsManager.equipPlayersWithTeamArmor()

        // Resetting player stats
        Bukkit.getOnlinePlayers().forEach { player ->
            player.gameMode = GameMode.ADVENTURE
            player.exp = 0.0F
            player.level = 0
            player.foodLevel = 20
            player.health = 20.0
        }

        this.playingPhaseTask = PlayingPhaseTask(this.main).runTaskTimer(this.main, 0, 20)
    }

    fun isGameStarting(): Boolean = this.gameState == GameState.STARTING
    fun hasGameStarted(): Boolean = this.gameState > GameState.STARTING
    fun isGameFinished(): Boolean = this.gameState >= GameState.FINISHED

    fun triggerFinishedGamePhase(winningTeam: TeamsManager.GameTeam) {
        this.gameState = GameState.FINISHED
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX}${ChatColor.YELLOW} L'${ChatColor.GOLD}${winningTeam.displayName}${ChatColor.YELLOW} a gagné ! ${ChatColor.AQUA}$CONGRATS_DECORATION ${ChatColor.RESET}${ChatColor.YELLOW}Félicitations $CONGRATS_DECORATION_REVERSED")

        // Resetting player stats
        Bukkit.getOnlinePlayers().forEach { player ->
            player.gameMode = GameMode.CREATIVE
            player.exp = 0.0F
            player.level = 0
            player.foodLevel = 20
            player.health = 20.0
            player.inventory.clear()
        }
    }
}
