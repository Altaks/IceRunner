package fr.altzec.fr.altzec.icerunner.game

import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.utils.ExposedFunctions.length
import fr.altzec.fr.altzec.icerunner.utils.StatusBarUtils
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.log10

class ScoreboardManager(val main: Main) : Listener {

    companion object {
        private const val NO_DELAY = 0L
        private const val EVERY_SECOND = 20L

        // Team name and spacing calculation
        private const val WAITING_LINE_LENGTH = 17

        private const val WAITING_TEAM_COUNTER_LENGTH = 3
        private const val WAITING_TEAM_DECORATION_CHARACTER_LENGTH = 1
        private const val WAITING_TEAM_DISPLAY_SPACEFILL_LENGTH = WAITING_LINE_LENGTH - WAITING_TEAM_COUNTER_LENGTH - WAITING_TEAM_DECORATION_CHARACTER_LENGTH

        private const val WAITING_TEAM_STATUS_BAR_STYLE_SYMBOL = "◆"

        private const val WAITING_STATUS_BAR_STATUS_BAR_LENGTH = 7; // 7 players, one diamond per player
        private const val WAITING_STATUS_BAR_STYLE_DECORATION_CHARACTER_LENGTH = 2 * 1; // One > and a < on the sides.
        private const val WAITING_STATUS_BAR_SPACEFILL_LENGTH = (WAITING_LINE_LENGTH - WAITING_STATUS_BAR_STATUS_BAR_LENGTH - WAITING_STATUS_BAR_STYLE_DECORATION_CHARACTER_LENGTH) / 2

        fun spaceFilledScore(score: Int) = "${" ".repeat(3 - score.length())}$score"
    }

    val playerScoreboards: HashMap<Player, FastBoard> = HashMap()

    @EventHandler
    fun onPlayerJoins(event: PlayerJoinEvent) = initPlayerScoreboard(event.player)

    @EventHandler
    fun onPlayerQuits(event: PlayerQuitEvent) = unloadPlayerScoreboard(event.player)

    fun initPlayerScoreboard(player: Player) {
        val board = FastBoard(player)
        board.updateTitle(Main.GAME_NAME)
        playerScoreboards[player] = board
    }

    fun unloadPlayerScoreboard(player: Player) {
        playerScoreboards[player]?.delete()
        playerScoreboards.remove(player)
    }

    fun initScoreboardUpdating() {
        ScoreboardUpdatingTask(main).runTaskTimerAsynchronously(main, NO_DELAY, EVERY_SECOND)
    }

    private class ScoreboardUpdatingTask(val main: Main) : BukkitRunnable() {
        override fun run() {
            if (!main.gameManager.hasGameStarted()) {
                this.main.scoreboardManager.playerScoreboards.entries.forEach { (_, scoreboard) -> updateWaitingScoreboard(scoreboard) }
            } else {
                this.main.scoreboardManager.playerScoreboards.entries.forEach { (player, scoreboard) -> updatePlayingScoreboard(player, scoreboard, this.main.teamsManager.getGameScoringState()) }
            }
        }

        private fun updateWaitingScoreboard(board: FastBoard) {
            val boardLines = mutableListOf<String>()

            for ((minecraftTeam, gameTeam) in this.main.teamsManager.getTeamsToGameTeamMapping()) {
                val teamLineSpaceFill = WAITING_TEAM_DISPLAY_SPACEFILL_LENGTH - gameTeam.displayName.length
                val teamStatusBarState = StatusBarUtils.StatusBarState(
                    WAITING_STATUS_BAR_STATUS_BAR_LENGTH,
                    TeamsManager.PLAYERS_PER_TEAM,
                    minecraftTeam.entries.size,
                )
                val teamStatusBarStyle = StatusBarUtils.StatusBarStyle(activeColor = gameTeam.chatColor, baseSymbol = WAITING_TEAM_STATUS_BAR_STYLE_SYMBOL, activeSymbol = WAITING_TEAM_STATUS_BAR_STYLE_SYMBOL)

                boardLines.add("")
                boardLines.add("${gameTeam.displayName}:${" ".repeat(teamLineSpaceFill)}${minecraftTeam.entries.size}/${TeamsManager.PLAYERS_PER_TEAM}")
                boardLines.add("${" ".repeat(WAITING_STATUS_BAR_SPACEFILL_LENGTH)}\u00BB${StatusBarUtils.buildProgressBar(teamStatusBarState, teamStatusBarStyle)}\u00AB${" ".repeat(WAITING_STATUS_BAR_SPACEFILL_LENGTH)}")
            }

            boardLines.add("")

            if (!this.main.gameManager.isGameStarting()) {
                boardLines.add("  Il manque ${TeamsManager.PLAYERS_REQUIRED_TO_START_GAME - Bukkit.getOnlinePlayers().size}  ")
                boardLines.add("      joueurs")
            }

            board.updateLines(boardLines)
        }

        private fun updatePlayingScoreboard(player: Player, scoreboard: FastBoard, gameScoringState: TeamsManager.GameScoringState) {
            val playerTeamColor = player.scoreboard.getEntryTeam(player.name)?.color;
            val boardLines = when(playerTeamColor) {
                ChatColor.RED -> buildRedSideScoreboard(gameScoringState)
                ChatColor.AQUA -> buildBlueSideScoreboard(gameScoringState)
                else -> throw IllegalStateException("This team is null / not supported")
            }
            scoreboard.updateLines(boardLines)
        }

        private fun buildBlueSideScoreboard(gameScoringState: TeamsManager.GameScoringState) : List<String> {

            val yellowIslandColor = gameScoringState.yellowIslandDominatedBy?.chatColor ?: ChatColor.YELLOW;
            val greenIslandColor = gameScoringState.greenIslandDominatedBy?.chatColor ?: ChatColor.GREEN;
            val centerIslandColor = gameScoringState.centerIslandDominatedBy?.chatColor ?: ChatColor.WHITE;

            return listOf(
                "",
                "   ${ChatColor.RED}✦ ${spaceFilledScore(gameScoringState.redTeamScore)}${ChatColor.RESET}/360",
                "",
                "   ${yellowIslandColor}⬛${ChatColor.RESET}        ${ChatColor.RED}⬛⬛${ChatColor.RESET}  ",
                "               ${ChatColor.RED}⬛${ChatColor.RESET}  ",
                "        $centerIslandColor⬛⬛${ChatColor.RESET}",
                "        $centerIslandColor⬛⬛${ChatColor.RESET}",
                "   ${ChatColor.AQUA}⬛${ChatColor.RESET}             ",
                "   ${ChatColor.AQUA}⬛⬛${ChatColor.RESET}        ${greenIslandColor}⬛${ChatColor.RESET}  ",
                "",
                "   ${ChatColor.AQUA}❉ ${spaceFilledScore(gameScoringState.blueTeamScore)}${ChatColor.RESET}/360",
                "",
            )
        }

        private fun buildRedSideScoreboard(gameScoringState: TeamsManager.GameScoringState) : List<String> {

            val yellowIslandColor = gameScoringState.yellowIslandDominatedBy?.chatColor ?: ChatColor.YELLOW;
            val greenIslandColor = gameScoringState.greenIslandDominatedBy?.chatColor ?: ChatColor.GREEN;
            val centerIslandColor = gameScoringState.centerIslandDominatedBy?.chatColor ?: ChatColor.WHITE;

            return listOf(
                "",
                "   ${ChatColor.AQUA}❉ ${spaceFilledScore(gameScoringState.blueTeamScore)}${ChatColor.RESET}/360",
                "",
                "   ${greenIslandColor}⬛${ChatColor.RESET}        ${ChatColor.AQUA}⬛⬛${ChatColor.RESET}  ",
                "               ${ChatColor.AQUA}⬛${ChatColor.RESET}  ",
                "        $centerIslandColor⬛⬛${ChatColor.RESET}",
                "        $centerIslandColor⬛⬛${ChatColor.RESET}",
                "   ${ChatColor.RED}⬛${ChatColor.RESET}             ",
                "   ${ChatColor.RED}⬛⬛${ChatColor.RESET}        ${yellowIslandColor}⬛${ChatColor.RESET}  ",
                "",
                "   ${ChatColor.RED}✦ ${spaceFilledScore(gameScoringState.redTeamScore)}${ChatColor.RESET}/360",
                ""
            )
        }
    }
}
