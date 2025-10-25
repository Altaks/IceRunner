package fr.altzec.fr.altzec.icerunner.utils

import org.bukkit.ChatColor

object StatusBarUtils {

    data class StatusBarState(
        val length: Int,

        val totalCapacity: Int,
        val currentCount: Int,
    )

    data class StatusBarStyle(
        val baseColor: ChatColor = ChatColor.GRAY,
        val baseSymbol: String = "□",

        val activeColor: ChatColor = ChatColor.GREEN,
        val activeSymbol: String = "■",

        val leftToRight: Boolean = true,
    )

    /**
     * Default the statusBarStyle with the default values of the data class
     */
    fun buildProgressBar(progressBarState: StatusBarState, statusBarStyle: StatusBarStyle = StatusBarStyle()): String {
        require(progressBarState.totalCapacity >= 0) { "Progress bar total capacity cannot be 0" }
        require(progressBarState.length >= 0) { "Progress bar length cannot be 0" }
        require(progressBarState.currentCount <= progressBarState.totalCapacity) { "Progress bar currentCount: ${progressBarState.currentCount} cannot be higher than totalCapacity : ${progressBarState.totalCapacity}" }

        val activePercentage = progressBarState.currentCount.toFloat() / progressBarState.totalCapacity.toFloat()
        val activeLength = (progressBarState.length * activePercentage).toInt()
        val inactiveLength = progressBarState.length - activeLength

        check(activeLength + inactiveLength == progressBarState.length)
        check(activeLength >= 0)

        val activePart = "${statusBarStyle.activeColor}${statusBarStyle.activeSymbol.repeat(activeLength)}"
        val inactivePart = "${statusBarStyle.baseColor}${statusBarStyle.baseSymbol.repeat(inactiveLength)}"

        return if (statusBarStyle.leftToRight) {
            activePart + inactivePart
        } else {
            inactivePart + activePart
        }
    }
}
