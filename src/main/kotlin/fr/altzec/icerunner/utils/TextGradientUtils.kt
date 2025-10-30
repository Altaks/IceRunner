package fr.altzec.fr.altzec.icerunner.utils

import net.md_5.bungee.api.ChatColor
import java.awt.Color

object TextGradientUtils {

    /**
     * Generates a gradient string between two colors
     * @param text The text to apply the gradient to
     * @param startColor Starting color in hex format (e.g., "#FF0000" or "FF0000")
     * @param endColor Ending color in hex format (e.g., "#00FF00" or "00FF00")
     * @return The text with gradient applied using BungeeCord ChatColor API
     */
    fun generateGradient(text: String, startColor: String, endColor: String): String {
        // Remove # if present
        val cleanStartColor = startColor.replace("#", "")
        val cleanEndColor = endColor.replace("#", "")

        // Parse colors
        val start = Color.decode("#$cleanStartColor")
        val end = Color.decode("#$cleanEndColor")

        // Remove existing color codes
        val cleanText = ChatColor.stripColor(text) ?: return ""

        if (cleanText.isEmpty()) {
            return ""
        }

        val result = StringBuilder()

        // Calculate gradient for each character
        cleanText.forEachIndexed { i, char ->
            // Calculate interpolation factor
            val ratio = i.toFloat() / (cleanText.length - 1)

            // Interpolate between start and end colors
            val r = (start.red + ratio * (end.red - start.red)).toInt()
            val g = (start.green + ratio * (end.green - start.green)).toInt()
            val b = (start.blue + ratio * (end.blue - start.blue)).toInt()

            // Create hex color string
            val hexColor = "#%02x%02x%02x".format(r, g, b)

            // Apply color using BungeeCord ChatColor API
            val color = ChatColor.of(hexColor)
            result.append(color).append(char)
        }

        return result.toString()
    }
}
