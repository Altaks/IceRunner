package fr.altzec.fr.altzec.icerunner.triggers.commands

import fr.altzec.fr.altzec.icerunner.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DevCommand(val main: Main) : TabExecutor {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String?>,
    ): List<String?>? {
        if (args.isNotEmpty()) {
            return listOf("startGame", "endGame", "playingGame", "genWorld")
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
            when (args.first()) {
                "startGame" -> this.main.gameManager.triggerStartingGamePhase()
                "playingGame" -> this.main.gameManager.triggerPlayingGamePhase()
                "endGame" -> this.main.gameManager.triggerFinishedGamePhase(this.main.teamsManager.getPlayerGameTeam(sender))
                "genWorld" -> this.main.worldManager.setupGameWorld()
                else -> {
                    sender.sendMessage("This command sub-argument matches nothing")
                    return false
                }
            }
            return true
        }
        return false
    }
}
