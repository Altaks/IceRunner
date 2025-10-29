package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.triggers.tasks.PlayingPhaseTask
import fr.altzec.fr.altzec.icerunner.triggers.tasks.StartingPhaseTask
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitTask

class GameManager(val main: Main) {

    private var gameState: GameState = GameState.WAITING

    private var startingPhaseTask: BukkitTask? = null
    private var playingPhaseTask: BukkitTask? = null

    fun triggerStartingGamePhase() {
        this.gameState = GameState.STARTING
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX} Starting game...")

        this.main.worldManager.setupGameWorld()
        this.main.worldManager.teleportPlayersToGameWorld()

        // Starting and storing the startingPhaseTask
        this.startingPhaseTask = StartingPhaseTask(this.main).runTaskTimer(this.main, 0, 20)
    }

    fun triggerPlayingGamePhase() {
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

        this.playingPhaseTask = PlayingPhaseTask(this.main).runTaskTimer(this.main, 0, 20);
    }

    fun isGameStarting(): Boolean = this.gameState == GameState.STARTING
    fun hasGameStarted(): Boolean = this.gameState > GameState.STARTING
    fun isGameFinished(): Boolean = this.gameState >= GameState.FINISHED

    fun triggerFinishedGamePhase(winningTeam: TeamsManager.GameTeam) {
        this.gameState = GameState.FINISHED
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX} Game is finished...$winningTeam has won !")

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
