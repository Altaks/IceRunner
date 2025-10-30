package fr.altzec.icerunner.game

import fr.altzec.fr.altzec.icerunner.game.ScoreboardManager
import org.junit.jupiter.api.Test

open class ScoreBoardManagerTests {

    @Test
    fun testScoreFillWithZeroScore() {
        val expectedFormat = "  0"
        val resolvedFormat = ScoreboardManager.spaceFilledScore(0);

        assert(expectedFormat == resolvedFormat);
    }

    @Test
    fun testScoreFillWithTwoDigits() {
        val expectedFormat = " 69"
        val resolvedFormat = ScoreboardManager.spaceFilledScore(69);

        assert(expectedFormat == resolvedFormat);
    }

    @Test
    fun testScoreFillWithThreeDigits() {
        val expectedFormat = "360"
        val resolvedFormat = ScoreboardManager.spaceFilledScore(360);

        assert(expectedFormat == resolvedFormat);
    }
}