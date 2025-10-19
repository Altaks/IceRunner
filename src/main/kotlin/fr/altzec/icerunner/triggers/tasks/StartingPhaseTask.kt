package fr.altzec.fr.altzec.icerunner.triggers.tasks

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.game.GameManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable

class StartingPhaseTask(val main: Main, val gameManager: GameManager) : BukkitRunnable() {

    companion object {
        private const val STARTING_PHASE_COUNTDOWN: Int = 10
        private const val STARTING_PHASE_COUNTDOWN_LAST_VALUE: Int = 0
    }

    var countdown: Int = STARTING_PHASE_COUNTDOWN;

    override fun run() {
        if(countdown <= STARTING_PHASE_COUNTDOWN_LAST_VALUE) {
            // TODO : Teleport players to their own base
            this.cancel();
        } else countdown--;

        main.worldManager.loadedWorldMetadata?.mapCenterGlassCoordinates?.forEach { location ->
            val block: Block = location.block
            block.setType(
                when (block.type) {
                    Material.WHITE_STAINED_GLASS -> Material.RED_STAINED_GLASS
                    Material.RED_STAINED_GLASS -> Material.CYAN_STAINED_GLASS
                    Material.CYAN_STAINED_GLASS -> Material.RED_STAINED_GLASS
                    else -> block.type
                }, false)
        }
    }
}