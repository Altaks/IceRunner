package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.triggers.tasks.StartingPhaseTask
import org.bukkit.Bukkit

class GameManager(val main: Main) {

    companion object {
        const val PLAYERS_PER_TEAM = 1
        const val AMOUNT_OF_TEAMS = 2

        const val PLAYERS_REQUIRED_TO_START_GAME = PLAYERS_PER_TEAM * AMOUNT_OF_TEAMS
    }

    private var gameState: GameState = GameState.WAITING

    fun startGame() {
        this.gameState = GameState.STARTING
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX} Starting game...")

        this.main.worldManager.setupGameWorld()
        this.main.worldManager.teleportPlayersToGameWorld()

        StartingPhaseTask(this.main).runTaskTimer(this.main, 0, 20)
    }

    fun hasGameStarted(): Boolean = this.gameState >= GameState.STARTING

    fun endGame() {
        this.gameState = GameState.FINISHED
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX} Game is finished...")
    }
}
