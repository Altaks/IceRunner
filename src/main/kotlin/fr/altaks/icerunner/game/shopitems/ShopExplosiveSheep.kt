package fr.altaks.icerunner.game.shopitems

import fr.altaks.icerunner.Main
import fr.altaks.icerunner.game.GameItems
import fr.altaks.icerunner.game.ShopManager
import fr.altaks.icerunner.utils.ItemComparator
import fr.altaks.icerunner.utils.ItemFactory
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Sheep
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.NumberConversions

class ShopExplosiveSheep(val main: Main) : ShopManager.Companion.IShopItem {

    companion object {
        private const val EXPLOSIVE_SHEEPS_TTL = 5 * 1000L; // 5 seconds
    }

    override fun cost(): UInt = 16u
    override fun item(): ItemStack = ItemFactory(Material.SHEEP_SPAWN_EGG, 1)
        .setDisplayName("${ChatColor.RED}☣ File un mauvais coton")
        .setLore(
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GRAY}Né des vents hurlants de Niflheim, ce mouton maudit bondit",
            "${ChatColor.GRAY}vers son destin dans un cri tonitruant. Guidé par ton",
            "${ChatColor.GRAY}regard, il charge l’ennemi avant d’exploser dans une",
            "${ChatColor.GRAY}lumière glacée et divine.",
            "${ChatColor.GRAY}${GameItems.loreDelimitation}",
            "${ChatColor.GOLD}⛁ ${cost()} ${if (cost() <= 1u) {
                "pièce"
            } else {
                "pièces"
            }}",
        )
        .build()

    override fun position(): Int = 6

    private val activeExplosiveSheepTTL = HashMap<Sheep, Long>()

    @EventHandler
    fun onPlayerUsesItem(event: PlayerInteractEvent) {
        if (event.item != null && ItemComparator.compare(event.item, item())) {
            event.isCancelled = true
            ensureExplosiveSheepTaskIsActive()
            spawnAndShootExplosiveSheep(event.player)
        }
    }

    private fun spawnAndShootExplosiveSheep(player: Player) {
        val eyePosition = player.eyeLocation
        val lookDirection = player.eyeLocation.direction
        val spawnLocation = eyePosition.add(lookDirection)
        val entity = spawnLocation.world?.spawnEntity(spawnLocation, EntityType.SHEEP)
        val sheep = entity as Sheep;

        sheep.color = DyeColor.BLACK;
        sheep.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 1_000_000, 10, false))
        sheep.velocity = player.eyeLocation.direction.multiply(2.5)

        activeExplosiveSheepTTL[sheep] = System.currentTimeMillis();
    }

    private var fireBallTask: BukkitTask? = null

    private fun ensureExplosiveSheepTaskIsActive() {
        if (fireBallTask == null) {
            this.fireBallTask = ExplosiveSheepTask(this.main, this).runTaskTimer(this.main, 0, 20L);
        }
    }

    private class ExplosiveSheepTask(val main: Main, val explosiveSheepHandler: ShopExplosiveSheep) : BukkitRunnable() {

        companion object {
            private const val EXPLOSION_EFFECT_RADIUS = 5;
            private val EXPLOSION_AFFECTED_BLOCKS = listOf<Material>(Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE)
        }

        override fun run() {
            val now = System.currentTimeMillis();
            val sheepToRemoveList = mutableListOf<Sheep>()

            explosiveSheepHandler.activeExplosiveSheepTTL.forEach { (sheep, spawnTimestamp) -> run {
                if(spawnTimestamp + EXPLOSIVE_SHEEPS_TTL <= now) {
                    explode(sheep.location)
                    sheepToRemoveList.add(sheep)
                } else {
                    sheep.color = when(sheep.color) {
                        DyeColor.BLACK -> DyeColor.WHITE
                        DyeColor.WHITE -> DyeColor.BLACK
                        else -> throw IllegalStateException("Explosive sheep shouldn't be of color : ${sheep.color}")
                    }
                }
            } }

            sheepToRemoveList.forEach { it -> run { explosiveSheepHandler.activeExplosiveSheepTTL.remove(it); it.remove() }}
        }

        private fun explode(location: Location) {
            for (x in -EXPLOSION_EFFECT_RADIUS..EXPLOSION_EFFECT_RADIUS) {
                for (y in -EXPLOSION_EFFECT_RADIUS..EXPLOSION_EFFECT_RADIUS) {
                    for (z in -EXPLOSION_EFFECT_RADIUS..EXPLOSION_EFFECT_RADIUS) {
                        // scanned block position
                        val position = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())

                        // if the scanned block is in the sphere of radius EXPLOSION_EFFECT_RADIUS
                        if (position.distanceSquared(location) <= NumberConversions.square(EXPLOSION_EFFECT_RADIUS.toDouble())) {
                            if(EXPLOSION_AFFECTED_BLOCKS.contains(position.block.type)) {
                                position.block.setType(Material.AIR, false)
                            }
                        }
                    }
                }
            }

            location.world?.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 50f, 1f)
            location.world?.spawnParticle(Particle.EXPLOSION, location, 50, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), 0.1)

            location.world
                ?.getNearbyEntities(location, EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble(), EXPLOSION_EFFECT_RADIUS.toDouble())
                ?.filter { entity -> entity.type == EntityType.PLAYER }
                ?.forEach { entity -> this.main.gameManager.respawnPlayer(entity as Player) }
        }
    }
}
