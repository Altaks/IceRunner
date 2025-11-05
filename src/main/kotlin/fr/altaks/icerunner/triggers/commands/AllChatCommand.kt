package fr.altaks.icerunner.triggers.commands

import fr.altaks.icerunner.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class AllChatCommand(val main: Main) : TabExecutor {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String?>,
    ): List<String?>? = null

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String?>,
    ): Boolean {
        if (this.main.gameManager.hasGameStarted()) {
            if (sender is Player && args.isNotEmpty() && command.name.equals("all", ignoreCase = true)) {
                val message = args.joinToString(" ")
                sender.chat("!$message")
                return true
            }
        } else {
            sender.sendMessage("${Main.MAIN_PREFIX} Cette commande n'est pas disponible en dehors des parties")
            return true
        }
        return false
    }
}
