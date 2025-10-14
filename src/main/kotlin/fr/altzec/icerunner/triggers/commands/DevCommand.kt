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
            return listOf("startGame", "endGame")
        }

        return listOf<String>()
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String?>,
    ): Boolean {
        if (sender is Player && args.isNotEmpty()) {
            when (args.first()) {
                "startGame" -> this.main.gameManager.startGame()
                "endGame" -> this.main.gameManager.endGame()
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
