package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems.loreDelimitation
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemComparator
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class ShopReinforcedBridge(val main: Main) : ShopManager.Companion.IShopItem {

    companion object {
        private const val YETI_SNEEZE_TTL = 2500; // 2.5 seconds
    }

    override fun cost(): UInt = 5u
    override fun item(): ItemStack = ItemFactory(Material.PHANTOM_MEMBRANE, 5)
        .setDisplayName("${ChatColor.BLUE}❄ Eternuement de Yéti")
        .setLore(
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GRAY}Artéfact maudit de neige tombée des montagnes de Jotunheim.",
            "${ChatColor.GRAY}En utilisant ces cristaux, un pont de glace solide se forme,",
            "${ChatColor.GRAY}$loreDelimitation",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()
    override fun position(): Int = 2

    private val sneezeTTL = HashMap<UUID, Long>()

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        if (event.item != null && ItemComparator.compare(event.item, item())) {
            event.isCancelled = true
            ensureFireballTaskIsActive()
            spawnBridgeProjectile(event.player)
        }
    }

    @EventHandler
    fun onChargeHitsBlock(event: ProjectileHitEvent) {
        if (event.entity.type == EntityType.BREEZE_WIND_CHARGE || event.entity.type == EntityType.BREEZE_WIND_CHARGE) {
            event.isCancelled = true
            event.entity.remove()
        }
    }

    @EventHandler
    fun onChargeExplodesOnGround(event: ExplosionPrimeEvent) {
        if (event.entity.type == EntityType.BREEZE_WIND_CHARGE || event.entity.type == EntityType.BREEZE_WIND_CHARGE) {
            event.isCancelled = true
            event.entity.remove()
        }
    }

    private fun spawnBridgeProjectile(player: Player) {
        val eyePosition = player.eyeLocation
        val lookDirection = player.eyeLocation.direction
        val spawnLocation = eyePosition.add(lookDirection)
        val entity = spawnLocation.world?.spawnEntity(spawnLocation, EntityType.BREEZE_WIND_CHARGE) ?: throw IllegalStateException("Couldn't spawn Yeti Sneeze")
        sneezeTTL[entity.uniqueId] = System.currentTimeMillis()
    }

    private var fireBallTask: BukkitTask? = null

    private fun ensureFireballTaskIsActive() {
        if (fireBallTask == null) {
            this.fireBallTask = YetiSneeze(this).runTaskTimer(this.main, 0, 1L)
        }
    }

    private class YetiSneeze(val bridgeHandler: ShopReinforcedBridge) : BukkitRunnable() {
        override fun run() {
            Bukkit.getWorlds().forEach { world ->
                world
                    ?.entities
                    ?.filter { entity -> entity.type == EntityType.BREEZE_WIND_CHARGE }
                    ?.forEach { windCharge ->
                        run iteration@{
                            // Kill the wind charge if it lived too long
                            if ((bridgeHandler.sneezeTTL[windCharge.uniqueId] ?: throw IllegalStateException("Couldn't get wind charge UUID within thrown Yeti snoozes")) + YETI_SNEEZE_TTL <= System.currentTimeMillis()) {
                                bridgeHandler.sneezeTTL.remove(windCharge.uniqueId)
                                windCharge.remove()
                                return@iteration
                            }

                            val position = windCharge.location.add(0.0, -2.0, 0.0)

                            // list of 4 blocks anchored in the base location
                            listOf(
                                position.block,
                                position.block.getRelative(BlockFace.NORTH),
                                position.block.getRelative(BlockFace.SOUTH),
                                position.block.getRelative(BlockFace.EAST),
                                position.block.getRelative(BlockFace.WEST),
                            ).forEach { block ->
                                run {
                                    // If there are some players near the block, don't place it
                                    block.world.getNearbyEntities(position, 1.0, 1.0, 1.0) { entity -> entity.type == EntityType.PLAYER }
                                        .isEmpty()
                                        .let {
                                            if (!it) {
                                                return
                                            }
                                        }

                                    // Replace Materials by match
                                    val newType = when (block.type) {
                                        Material.AIR -> Material.PACKED_ICE
                                        Material.PACKED_ICE -> Material.BLUE_ICE
                                        else -> position.block.type
                                    }

                                    block.setType(newType, false)
                                }
                            }
                        }
                    }
            }
        }
    }
}
