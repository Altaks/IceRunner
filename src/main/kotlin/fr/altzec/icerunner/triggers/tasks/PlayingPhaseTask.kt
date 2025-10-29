package fr.altzec.fr.altzec.icerunner.triggers.tasks

import fr.altzec.fr.altzec.icerunner.Main
import org.bukkit.scheduler.BukkitRunnable

class PlayingPhaseTask(val main: Main) : BukkitRunnable() {
    override fun run() {
        if(this.main.gameManager.isGameFinished()) {
            cancel();
            return;
        }
        this.main.teamsManager.triggerPointsCounting()
    }
}