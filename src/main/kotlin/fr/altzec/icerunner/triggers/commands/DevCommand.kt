package fr.altzec.fr.altzec.icerunner.triggers.commands

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.game.GameItems
import fr.altzec.fr.altzec.icerunner.triggers.commands.DevCommand.SubDevCommand.Companion.getFromInGameSubCommand
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DevCommand(val main: Main) : TabExecutor {

    enum class SubDevCommand(val inGameName: String) {
        START_GAME("startGame"),
        PLAYING_GAME("playingGame"),
        END_GAME("endGame"),

        GENERATE_WORLD("genWorld"),

        WAITING_INVENTORY("waitingInv"),
        PLAYING_INVENTORY("playingInv");

        override fun toString(): String {
            return inGameName
        }

        companion object {
            fun getFromInGameSubCommand(value: String) : SubDevCommand? {
                return SubDevCommand.entries.firstOrNull { it.inGameName == value }
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String?>,
    ): List<String?>? {
        if (args.isNotEmpty() && args.size <= 1) {
            return SubDevCommand.entries.map { it.inGameName }
        }
        return null
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String?>,
    ): Boolean {
        if (sender is Player && args.isNotEmpty()) {
            when (getFromInGameSubCommand(args.first()!!)) {

                // Game management
                SubDevCommand.START_GAME -> this.main.gameManager.triggerStartingGamePhase()
                SubDevCommand.PLAYING_GAME -> this.main.gameManager.triggerPlayingGamePhase()
                SubDevCommand.END_GAME -> this.main.gameManager.triggerFinishedGamePhase(this.main.teamsManager.getPlayerGameTeam(sender))

                // World management
                SubDevCommand.GENERATE_WORLD -> this.main.worldManager.setupGameWorld()

                // Inv management
                SubDevCommand.WAITING_INVENTORY -> GameItems.applyWaitingInventoryToPlayer(sender)
                SubDevCommand.PLAYING_INVENTORY -> GameItems.applyPlayingInventoryToPlayer(sender, Color.PURPLE)

                null -> {
                    sender.sendMessage("This command sub-argument matches nothing")
                    return false
                }
            }
            return true
        }
        return false
    }
}
