package fr.altzec.fr.altzec.icerunner.triggers.tasks

import fr.altzec.fr.altzec.icerunner.Main
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable

class StartingPhaseTask(val main: Main) : BukkitRunnable() {
    override fun run() {
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