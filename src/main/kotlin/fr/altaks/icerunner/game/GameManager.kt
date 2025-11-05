package fr.altaks.icerunner.game

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.triggers.tasks.ArrowTask
import fr.altaks.icerunner.triggers.tasks.BifrostTask
import fr.altaks.icerunner.triggers.tasks.PlayingPhaseTask
import fr.altaks.icerunner.triggers.tasks.StartingPhaseTask
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

class GameManager(val main: Main) : Listener {

    companion object {
        private const val NO_TASK_DELAY = 0L
        private const val EVERY_TICK = 1L
        private const val EVERY_SECOND = EVERY_TICK * 20L

        private const val INFINITE_POTION_EFFECT_DURATION = 1_000_000
        private const val JUMP_BOOST_AMPLIFIER = 1; // 2 blocks high

        private val CONGRATS_DECORATION = "${ChatColor.RED}${ChatColor.MAGIC}!${ChatColor.AQUA}${ChatColor.MAGIC}!${ChatColor.GREEN}${ChatColor.MAGIC}!${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}!${ChatColor.GOLD}${ChatColor.MAGIC}!"
        private val CONGRATS_DECORATION_REVERSED = "${ChatColor.GOLD}${ChatColor.MAGIC}!${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}!${ChatColor.GREEN}${ChatColor.MAGIC}!${ChatColor.AQUA}${ChatColor.MAGIC}!${ChatColor.RED}${ChatColor.MAGIC}!"
    }

    private var gameState: GameState = GameState.WAITING

    private var startingPhaseTask: BukkitTask? = null
    private var bifrostTask: BukkitTask? = null
    private var playingPhaseTask: BukkitTask? = null

    private var arrowTask: BukkitTask? = null

    fun triggerStartingGamePhase() {
        this.gameState = GameState.STARTING
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX}${ChatColor.LIGHT_PURPLE} La phase de sélection d'équipes commence !")

        this.main.worldManager.teleportPlayersToGameWorld()

        // Starting and storing the startingPhaseTask
        this.startingPhaseTask = StartingPhaseTask(this.main).runTaskTimer(this.main, NO_TASK_DELAY, EVERY_SECOND)
    }

    fun triggerPlayingGamePhase() {
        this.gameState = GameState.PLAYING
        Bukkit.broadcastMessage(
            "${Main.MAIN_PREFIX}${ChatColor.LIGHT_PURPLE} La partie commence !...\n" +
                "${ChatColor.GRAY}Le but du jeu est simple, capturer les différentes îles : \n" +
                "${ChatColor.GRAY}- L'île ${ChatColor.WHITE}blanche${ChatColor.GRAY}, située au centre rapporte le plus de points à votre équipe.\n" +
                "${ChatColor.GRAY}- Les îles secondaires (${ChatColor.GREEN}verte${ChatColor.GRAY} et ${ChatColor.YELLOW}jaune${ChatColor.GRAY}) rapportent aussi des points.\n" +
                "\n\n" +
                "${ChatColor.GRAY}Vous disposez de ${ChatColor.UNDERLINE}boules de neige${ChatColor.RESET}${ChatColor.GRAY} qui forment des ponts de glace pour vous rendrez sur les différents objectifs, mais aussi des ${ChatColor.UNDERLINE}flèches explosives${ChatColor.RESET}${ChatColor.GRAY} pour \u00AB briser la glace \u00BB avec vos adversaires.\n" +
                "${ChatColor.GRAY}Une équipe remporte la partie lorsqu'elle atteint ${ChatColor.UNDERLINE}360 points${ChatColor.RESET}${ChatColor.GRAY}.\n" +
                "${Main.MAIN_PREFIX} ${ChatColor.YELLOW}${ChatColor.GOLD}Bonne partie et bonne chance à tous !\n",
        )

        this.main.shopManager.preparePlayerShops()

        this.main.teamsManager.teleportPlayersToTheirTeamSpawnAndSetRespawnPoints()
        this.main.teamsManager.equipPlayersWithTeamEquipments()

        // Resetting player stats
        Bukkit.getOnlinePlayers().forEach { player ->
            player.gameMode = GameMode.ADVENTURE
            player.exp = 0.0F
            player.level = 0
            player.foodLevel = 20
            player.health = 20.0
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, INFINITE_POTION_EFFECT_DURATION, JUMP_BOOST_AMPLIFIER))
        }

        // Just for testing purposes
        this.bifrostTask = BifrostTask().runTaskTimer(this.main, NO_TASK_DELAY, EVERY_TICK)
        this.arrowTask = ArrowTask().runTaskTimer(this.main, NO_TASK_DELAY, EVERY_TICK)
        this.playingPhaseTask = PlayingPhaseTask(this.main).runTaskTimer(this.main, 0, 20)
    }

    fun isGameStarting(): Boolean = this.gameState == GameState.STARTING
    fun hasGameStarted(): Boolean = this.gameState > GameState.STARTING
    fun isGameFinished(): Boolean = this.gameState >= GameState.FINISHED

    fun triggerFinishedGamePhase(winningTeam: TeamsManager.GameTeam) {
        this.gameState = GameState.FINISHED
        Bukkit.broadcastMessage("${Main.MAIN_PREFIX}${ChatColor.YELLOW} L'${ChatColor.GOLD}${winningTeam.displayName}${ChatColor.YELLOW} a gagné ! ${ChatColor.AQUA}$CONGRATS_DECORATION ${ChatColor.RESET}${ChatColor.YELLOW}Félicitations $CONGRATS_DECORATION_REVERSED")

        // Resetting player stats
        Bukkit.getOnlinePlayers().forEach { player ->
            player.gameMode = GameMode.CREATIVE
            player.exp = 0.0F
            player.level = 0
            player.foodLevel = 20
            player.health = 20.0
            player.inventory.clear()
        }
    }

    @EventHandler
    fun onPlayerDies(event: PlayerDeathEvent) {
        respawnPlayer(event.entity)
    }

    @EventHandler
    fun onPlayerFallsIntoTheVoid(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.VOID) return
        if (event.entity is Player) {
            event.isCancelled = true
            respawnPlayer(event.entity as Player)
        }
    }

    @EventHandler
    fun onPlayerHungerChanges(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityTakesFallDamage(event: EntityDamageEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDropsItem(event: PlayerDropItemEvent) {
        // No matter the game state, no one should be able to drop items
        event.isCancelled = true
    }

    fun respawnPlayer(player: Player) {
        when (this.gameState) {
            GameState.PLAYING -> {
                // Cancel the event first
                val playerTeam = this.main.teamsManager.getPlayerGameTeam(player)

                // Teleport them to their base
                val respawnPoint = playerTeam.respawnPoint(this.main.worldManager.loadedWorldMetadata ?: throw IllegalStateException("The loaded world variant metadata should exist"))
                player.teleport(respawnPoint)

                player.exp = 0.0F
                player.level = 0

                player.activePotionEffects.forEach { effect -> player.removePotionEffect(effect.type) }
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, INFINITE_POTION_EFFECT_DURATION, JUMP_BOOST_AMPLIFIER))

                // Reapply inventory to player
                GameItems.applyPlayingInventoryToPlayer(player, playerTeam.armorColor, this.main.shopManager.getPlayerMoney(player))
            }
            else -> {
                player.teleport(this.main.worldManager.loadedWorldMetadata?.mapCenterCoordinates?.add(0.0, 1.5, 0.0) ?: throw IllegalStateException("Unable to acquire map center coordinates"))
            }
        }
    }
}
