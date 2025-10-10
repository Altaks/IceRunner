package fr.altzec

import lombok.Getter
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin () {

    @Getter
    private val pluginLogger = Bukkit.getLogger();

    override fun onEnable() {
        super.onEnable();
        pluginLogger.info("onEnable function has finished");
    }

}