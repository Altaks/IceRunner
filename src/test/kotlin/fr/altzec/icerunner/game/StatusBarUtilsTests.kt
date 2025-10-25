@file:Suppress("DEPRECATION")

package fr.altzec.icerunner.game

import fr.altzec.fr.altzec.icerunner.utils.StatusBarUtils
import org.bukkit.ChatColor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

open class StatusBarUtilsTests {

    @Test
    fun testGenerateBasicStatusBar() {
        val barState = StatusBarUtils.StatusBarState(7, 7, 4);
        val expectedStatusBar = "${ChatColor.GREEN}■■■■${ChatColor.GRAY}□□□";

        val statusBar = StatusBarUtils.buildProgressBar(barState);
        assert(statusBar == expectedStatusBar)
    }

    @Test
    fun testGenerateRTLBasicStatusBar() {
        val barState = StatusBarUtils.StatusBarState(2, 100, 50);
        val expectedStatusBar = "${ChatColor.GRAY}□${ChatColor.GREEN}■";

        val statusBar = StatusBarUtils.buildProgressBar(barState, StatusBarUtils.StatusBarStyle(leftToRight = false));
        assert(statusBar == expectedStatusBar)
    }

    @Test
    fun testGenerateNonDefaultSymbolStatusBar() {
        val barStyle = StatusBarUtils.StatusBarStyle(activeSymbol = ")", baseSymbol = "!");
        val barState = StatusBarUtils.StatusBarState(2, 100, 50);
        val expectedStatusBar = "${ChatColor.GREEN})${ChatColor.GRAY}!";

        val statusBar = StatusBarUtils.buildProgressBar(barState, barStyle);
        assert(statusBar == expectedStatusBar)
    }

    @Test
    fun testGenerateNonDefaultColorsStatusBar() {
        val barStyle = StatusBarUtils.StatusBarStyle(activeColor = ChatColor.AQUA, baseColor = ChatColor.GOLD);
        val barState = StatusBarUtils.StatusBarState(2, 100, 50);
        val expectedStatusBar = "${ChatColor.AQUA}■${ChatColor.GOLD}□";

        val statusBar = StatusBarUtils.buildProgressBar(barState, barStyle);
        assert(statusBar == expectedStatusBar)
    }

    @Test
    fun testGenerateWaitingPhaseRedTeamStatusBar() {
        val teamSize = 7;
        val teamCurrentPlayersAmount = Random.nextInt(0, 7);

        val barStyle = StatusBarUtils.StatusBarStyle(baseSymbol = "◆", activeSymbol = "◆", activeColor = ChatColor.RED)
        val barState = StatusBarUtils.StatusBarState(teamSize, teamSize, teamCurrentPlayersAmount);

        val expectedStatusBar =  "${ChatColor.RED}${"◆".repeat(teamCurrentPlayersAmount)}${ChatColor.GRAY}${"◆".repeat(7 - teamCurrentPlayersAmount)}"
        val statusBar = StatusBarUtils.buildProgressBar(barState, barStyle);
        assert(statusBar == expectedStatusBar);
    }


    @Test
    fun testGenerateWaitingPhaseBlueTeamStatusBar() {
        val teamSize = 7;
        val teamCurrentPlayersAmount = Random.nextInt(0, 7);

        val barStyle = StatusBarUtils.StatusBarStyle(baseSymbol = "◆", activeSymbol = "◆", activeColor = ChatColor.AQUA)
        val barState = StatusBarUtils.StatusBarState(teamSize, teamSize, teamCurrentPlayersAmount);

        val expectedStatusBar = "${ChatColor.AQUA}${"◆".repeat(teamCurrentPlayersAmount)}${ChatColor.GRAY}${"◆".repeat(7 - teamCurrentPlayersAmount)}"
        val statusBar = StatusBarUtils.buildProgressBar(barState, barStyle);
        assert(statusBar == expectedStatusBar);
    }

    @Test
    fun testAssertThrowsWhenCapacityIsNegative() {
        val barState = StatusBarUtils.StatusBarState(7, -1, 1);
        assertThrows<IllegalArgumentException> { StatusBarUtils.buildProgressBar(barState) }
    }

    @Test
    fun testAssertThrowWhenLengthIsNegative() {
        val barState = StatusBarUtils.StatusBarState(-1, 7, 1);
        assertThrows<IllegalArgumentException> { StatusBarUtils.buildProgressBar(barState) }
    }

    @Test
    fun testAssertDoesNotThrowWhenCurrentCountItAtTotalCapacity() {
        val barState = StatusBarUtils.StatusBarState(7, 7, 7);
        assertDoesNotThrow { StatusBarUtils.buildProgressBar(barState) }
    }

    @Test
    fun testAssertThrowWhenCurrentCountIsHigherThanTotalCapacity() {
        val barState = StatusBarUtils.StatusBarState(0, 1, 2);
        assertThrows<IllegalArgumentException> { StatusBarUtils.buildProgressBar(barState) }
    }

}