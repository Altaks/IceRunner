package fr.altzec.fr.altzec.icerunner.game

import com.google.common.collect.HashBiMap
import fr.altzec.fr.altzec.icerunner.Main
import fr.altzec.icerunner.utils.ItemComparator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Team

class TeamsManager(val main: Main) : Listener {

    data class GameTeam(
        val minecraftTeamId: String,
        val displayName: String,
        val prefix: Char,
        val color: ChatColor, // https://minecraft.fandom.com/wiki/Dye for Dye color codes
        val choiceItem: ItemStack
    )

    companion object {
        private val redTeam = GameTeam("RedTeam", "Equipe rouge", '❉', ChatColor.RED, GameItems.redTeamTag) // "B02E26"
        private val blueTeam = GameTeam("BlueTeam", "Equipe bleue", '✦', ChatColor.BLUE, GameItems.blueTeamTag) // "3C44AA"

        private val teams: List<GameTeam> = listOf(redTeam, blueTeam)
    }

    private val teamToGameTeamMapping: HashBiMap<Team, GameTeam> = HashBiMap.create<Team, GameTeam>(teams.size)
    private val mainScoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: throw IllegalStateException("Unable to access main scoreboard")

    fun prepareTeams() {

        // Remove all existing teams
        mainScoreboard.teams.forEach { team -> team.unregister() }

        // Setup game teams
        for (team in teams) {
            // Create the team
            val minecraftTeam = mainScoreboard.registerNewTeam(team.minecraftTeamId);
            minecraftTeam.color = team.color;
            minecraftTeam.displayName = "${team.color}${team.displayName}"
            minecraftTeam.setAllowFriendlyFire(false)
            minecraftTeam.prefix = "${team.color}${team.prefix} "

            // Insert into the team registry
            teamToGameTeamMapping[minecraftTeam] = team
        }
    }

    @EventHandler
    fun onPlayerChangesTeam(event: PlayerInteractEvent) {
        if (!event.hasItem()) return
        if (main.gameManager.hasGameStarted()) return

        main.logger.info("Player has clicked with item : ${event.item}")

        when {
            ItemComparator.compare(GameItems.redTeamTag, event.item) -> changePlayerTeam(event.player, redTeam)
            ItemComparator.compare(GameItems.blueTeamTag, event.item) -> changePlayerTeam(event.player, blueTeam)
            else -> main.logger.info("Clicked item does not match any condition on onPlayerChangesTeam")
        }
    }

    private fun changePlayerTeam(player: Player, targetTeam: GameTeam) {
        val minecraftTeam = teamToGameTeamMapping.inverse()[targetTeam] ?: throw IllegalStateException("Team ${targetTeam.displayName} is not mapped to a Minecraft team");
        minecraftTeam.addEntry(player.name)
    }

    fun getPlayerGameTeam(minecraftTeam: Team): GameTeam? {
        return teamToGameTeamMapping[minecraftTeam]
    }
}