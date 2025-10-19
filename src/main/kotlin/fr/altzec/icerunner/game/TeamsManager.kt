package fr.altzec.fr.altzec.icerunner.game

import com.google.common.collect.HashBiMap
import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.icerunner.utils.ItemComparator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
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
        val choiceItem: ItemStack
    )

    companion object {
        const val PLAYERS_PER_TEAM = 1
        const val AMOUNT_OF_TEAMS = 2

        const val PLAYERS_REQUIRED_TO_START_GAME = PLAYERS_PER_TEAM * AMOUNT_OF_TEAMS

        private val redTeam = GameTeam("RedTeam", "Equipe rouge", '✦', ChatColor.RED, Color.RED,GameItems.redTeamTag) // "B02E26"
        private val blueTeam = GameTeam("BlueTeam", "Equipe bleue", '❉', ChatColor.AQUA, Color.AQUA, GameItems.blueTeamTag) // "3AB3DA"

        private val teams: List<GameTeam> = listOf(redTeam, blueTeam)
    }

    private val teamToGameTeamMapping: HashBiMap<Team, GameTeam> = HashBiMap.create<Team, GameTeam>(teams.size)

    private fun getMainScoreboard(): Scoreboard { return Bukkit.getScoreboardManager()?.mainScoreboard ?: throw IllegalStateException("Unable to access main scoreboard") }

    fun prepareTeams() {

        // Remove all existing teams
        this.getMainScoreboard().teams.forEach { team -> main.logger.info("Unregister team ${team.displayName}"); team.unregister(); }

        // Setup game teams
        for (team in teams) {
            // Create the team
            val minecraftTeam = getMainScoreboard().registerNewTeam(team.minecraftTeamId);
            minecraftTeam.color = team.chatColor;
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

    private fun changePlayerTeam(player: Player, targetTeam: GameTeam) {
        val minecraftTeam = teamToGameTeamMapping.inverse()[targetTeam] ?: throw IllegalStateException("Team ${targetTeam.displayName} is not mapped to a Minecraft team");
        minecraftTeam.addEntry(player.name)
        main.logger.info("${player.displayName} is now in the ${targetTeam.displayName}")
    }

    fun areEnoughPlayersPerTeam(): Boolean = this.teamToGameTeamMapping.keys.all { team -> team.entries.size >= PLAYERS_PER_TEAM }

    fun getPlayerGameTeam(player: Player): GameTeam {
        val team: Team = this.getMainScoreboard().getEntryTeam(player.name) ?: throw IllegalStateException("Player ${player.name} has no team !")
        return this.teamToGameTeamMapping.get(team) ?: throw IllegalStateException("Team ${team.displayName} is not mapped to a GameTeam!")
    }

    fun teleportPlayersToTheirTeamSpawnAndSetRespawnPoints(): Unit {
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
}