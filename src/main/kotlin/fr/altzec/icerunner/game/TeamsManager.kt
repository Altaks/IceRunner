package fr.altzec.fr.altzec.icerunner.game

import com.google.common.collect.HashBiMap
import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.fr.altzec.icerunner.world.WorldManager
import fr.altzec.icerunner.utils.ItemComparator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class TeamsManager(val main: Main) : Listener {

    data class GameTeam(
        val minecraftTeamId: String,
        val displayName: String,
        val prefix: Char,
        val chatColor: ChatColor, // https://minecraft.fandom.com/wiki/Dye for Dye color codes
        val armorColor: Color,
        val choiceItem: ItemStack,
    )

    data class GameScoringState(
        var redTeamScore: Int,
        var blueTeamScore: Int,

        var centerIslandDominatedBy: GameTeam?,
        var greenIslandDominatedBy: GameTeam?,
        var yellowIslandDominatedBy: GameTeam?,
    )

    companion object {
        const val PLAYERS_PER_TEAM = 1
        const val AMOUNT_OF_TEAMS = 2

        const val PLAYERS_REQUIRED_TO_START_GAME = PLAYERS_PER_TEAM * AMOUNT_OF_TEAMS

        private val redTeam = GameTeam("RedTeam", "Équipe rouge", '✦', ChatColor.RED, Color.RED, GameItems.redTeamTag) // "B02E26"
        private val blueTeam = GameTeam("BlueTeam", "Équipe bleue", '❉', ChatColor.AQUA, Color.AQUA, GameItems.blueTeamTag) // "3AB3DA"

        private val teams: List<GameTeam> = listOf(redTeam, blueTeam)

        private const val CENTER_ISLAND_CAPTURE_POINTS_DELTA = +2
        private const val SECONDARY_ISLAND_CAPTURE_POINTS_DELTA = +1

        private const val POINTS_TO_ACHIEVE_VICTORY = 360
    }

    private val teamToGameTeamMapping: HashBiMap<Team, GameTeam> = HashBiMap.create<Team, GameTeam>(teams.size)
    private val teamToScoreMapping: HashMap<GameTeam, Int> = teams.associateWith { 0 } as HashMap

    private val gameScoringState = GameScoringState(
        redTeamScore = 0,
        blueTeamScore = 0,
        centerIslandDominatedBy = null,
        greenIslandDominatedBy = null,
        yellowIslandDominatedBy = null,
    )

    private fun getMainScoreboard(): Scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: throw IllegalStateException("Unable to access main scoreboard")

    fun prepareTeams() {
        // Remove all existing teams
        this.getMainScoreboard().teams.forEach { team ->
            main.logger.info("Unregister team ${team.displayName}")
            team.unregister()
        }

        // Setup game teams
        for (team in teams) {
            // Create the team
            val minecraftTeam = getMainScoreboard().registerNewTeam(team.minecraftTeamId)
            minecraftTeam.color = team.chatColor
            minecraftTeam.displayName = "${team.chatColor}${team.displayName}"
            minecraftTeam.setAllowFriendlyFire(false)
            minecraftTeam.prefix = "${team.chatColor}${team.prefix} "

            // Insert into the team registry
            teamToGameTeamMapping[minecraftTeam] = team
        }
    }

    @EventHandler
    fun onPlayerChangesTeam(event: PlayerInteractEvent) {
        if (!event.hasItem()) return
        if (main.gameManager.hasGameStarted()) return

        when {
            ItemComparator.compare(GameItems.redTeamTag, event.item) -> changePlayerTeam(event.player, redTeam)
            ItemComparator.compare(GameItems.blueTeamTag, event.item) -> changePlayerTeam(event.player, blueTeam)
            else -> return
        }
    }

    @EventHandler
    fun onPlayerDisconnects(event: PlayerQuitEvent) {
        if (!this.main.gameManager.hasGameStarted()) {
            // make the player quit their team
            event.player.scoreboard.getEntryTeam(event.player.name)?.removeEntry(event.player.name)
        }
    }

    private fun changePlayerTeam(player: Player, targetTeam: GameTeam) {
        val minecraftTeam = teamToGameTeamMapping.inverse()[targetTeam] ?: throw IllegalStateException("Team ${targetTeam.displayName} is not mapped to a Minecraft team")
        minecraftTeam.addEntry(player.name)
        main.logger.info("${player.displayName} is now in the ${targetTeam.displayName}")
    }

    fun areEnoughPlayersPerTeam(): Boolean = this.teamToGameTeamMapping.keys.all { team -> team.entries.size >= PLAYERS_PER_TEAM }

    fun getPlayerGameTeam(player: Player): GameTeam {
        val team: Team = this.getMainScoreboard().getEntryTeam(player.name) ?: throw IllegalStateException("Player ${player.name} has no team !")
        return this.teamToGameTeamMapping[team] ?: throw IllegalStateException("Team ${team.displayName} is not mapped to a GameTeam!")
    }

    fun getTeamsToGameTeamMapping(): HashBiMap<Team, GameTeam> = this.teamToGameTeamMapping

    fun teleportPlayersToTheirTeamSpawnAndSetRespawnPoints() {
        Bukkit.getOnlinePlayers().forEach { player ->
            val gameTeam = getPlayerGameTeam(player)

            val spawnLocation = when (gameTeam) {
                redTeam -> this.main.worldManager.loadedWorldMetadata?.redTeamSpawnCoordinates
                blueTeam -> this.main.worldManager.loadedWorldMetadata?.blueTeamSpawnCoordinates
                else -> null
            } ?: throw IllegalStateException("This GameTeam : $gameTeam is not supported !")

            player.setRespawnLocation(spawnLocation, true)
            player.teleport(spawnLocation)
        }
    }

    fun equipPlayersWithTeamArmor() {
        Bukkit.getOnlinePlayers().forEach { player ->
            GameItems.applyPlayingInventoryToPlayer(player, getPlayerGameTeam(player).armorColor)
        }
    }

    fun triggerPointsCounting() {
        this.main.worldManager.getIslandsVisitors().forEach { (island, players) ->
            run iteration@{
                if (players.isEmpty()) {
                    // No team dominates this island, thus dominant team color is null.
                    this.main.worldManager.updateIslandGlassWithTeamColor(island, null)
                    when (island) {
                        WorldManager.WorldIslands.CENTER -> this.gameScoringState.centerIslandDominatedBy = null
                        WorldManager.WorldIslands.GREEN -> this.gameScoringState.greenIslandDominatedBy = null
                        WorldManager.WorldIslands.YELLOW -> this.gameScoringState.yellowIslandDominatedBy = null
                    }
                    return@iteration
                }

                val teamToAmountOfPlayers = mutableMapOf<Team, Int>()
                players.forEach { player ->
                    run {
                        val team = player.scoreboard.getEntryTeam(player.name) ?: throw IllegalStateException("Couldn't get player team")
                        teamToAmountOfPlayers.putIfAbsent(team, 0)
                        teamToAmountOfPlayers[team] = teamToAmountOfPlayers[team]!! + 1
                    }
                }

                val dominantTeam = teamToAmountOfPlayers.maxBy { (_, amount) -> amount }.key; // TODO : change this team selection algorithm
                val gameTeam = teamToGameTeamMapping[dominantTeam] ?: throw IllegalStateException("This team is not registered as a GameTeam")

                // Update the island with the dominant team color
                this.main.worldManager.updateIslandGlassWithTeamColor(island, gameTeam.chatColor)

                when (island) {
                    WorldManager.WorldIslands.CENTER -> {
                        updateTeamScore(gameTeam, CENTER_ISLAND_CAPTURE_POINTS_DELTA)
                        this.gameScoringState.centerIslandDominatedBy = gameTeam
                    }
                    WorldManager.WorldIslands.GREEN -> {
                        updateTeamScore(gameTeam, SECONDARY_ISLAND_CAPTURE_POINTS_DELTA)
                        this.gameScoringState.greenIslandDominatedBy = gameTeam
                    }
                    WorldManager.WorldIslands.YELLOW -> {
                        updateTeamScore(gameTeam, SECONDARY_ISLAND_CAPTURE_POINTS_DELTA)
                        this.gameScoringState.yellowIslandDominatedBy = gameTeam
                    }
                }
            }
        }
    }

    private fun updateTeamScore(team: GameTeam, delta: Int) {
        this.main.pluginLogger.info("Team ${team.displayName} gains $delta points !")
        val newTeamScore = this.teamToScoreMapping[team]!! + delta

        if (newTeamScore >= POINTS_TO_ACHIEVE_VICTORY) {
            this.main.pluginLogger.info("Game is finished : ${team.displayName} has won !")
            this.main.gameManager.triggerFinishedGamePhase(team)
        } else {
            this.teamToScoreMapping[team] = this.teamToScoreMapping[team]!! + delta
            when (team) {
                redTeam -> this.gameScoringState.redTeamScore = newTeamScore
                blueTeam -> this.gameScoringState.blueTeamScore = newTeamScore
                else -> throw IllegalStateException("This team is not supported by the game scoring state data")
            }
        }
    }

    fun getGameScoringState() = gameScoringState
}
