package fr.altaks.icerunner.triggers.tasks

import fr.altaks.icerunner.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable

class StartingPhaseTask(val main: Main) : BukkitRunnable() {

    companion object {
        private const val STARTING_PHASE_COUNTDOWN_START_VALUE: Int = 15
        private const val STARTING_PHASE_COUNTDOWN_END_VALUE: Int = 0
    }

    var countdown: Int = STARTING_PHASE_COUNTDOWN_START_VALUE

    override fun run() {
        main.worldManager.loadedWorldMetadata?.mapCenterGlassCoordinates?.forEach { location ->
            val block: Block = location.block
            block.setType(
                when (block.type) {
                    Material.WHITE_STAINED_GLASS -> Material.RED_STAINED_GLASS
                    Material.RED_STAINED_GLASS -> Material.LIGHT_BLUE_STAINED_GLASS
                    Material.LIGHT_BLUE_STAINED_GLASS -> Material.RED_STAINED_GLASS
                    else -> block.type
                },
                false,
            )
        }

        if (main.teamsManager.areEnoughPlayersPerTeam()) {
            if (countdown <= STARTING_PHASE_COUNTDOWN_END_VALUE) {
                this.main.gameManager.triggerPlayingGamePhase()
                main.worldManager.loadedWorldMetadata?.mapCenterGlassCoordinates?.forEach { location -> location.block.type = Material.WHITE_STAINED_GLASS }
                this.cancel()
            } else {
                Bukkit.getOnlinePlayers().forEach { player -> player.sendTitle("${ChatColor.YELLOW}Démarrage dans ${ChatColor.GOLD}$countdown", "${ChatColor.YELLOW}secondes", 10, 70, 20) }
                countdown--
            }
        } else {
            countdown = STARTING_PHASE_COUNTDOWN_START_VALUE
        }
    }
}
