package kr.sul.worldmanager.summonmob

import net.minecraft.server.v1_12_R1.EntityMonster
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class MobCleaner(private val plugin: Plugin, parentNode: String, activeWorldsParam: List<World>) : Listener {
    companion object {
        val activeWorlds = ArrayList<World>()
        var CLEANING_MOB_PERIOD: Int = 999999999
    }
    init {
        activeWorlds.addAll(activeWorldsParam)
        CLEANING_MOB_PERIOD = plugin.config.getInt("$parentNode.cleaning-mob-period") * 20
        registerCleanUselessMobScheduler()
    }

    private fun registerCleanUselessMobScheduler() {
        object : BukkitRunnable() {
            override fun run() {
                cleanAutoSummonableMob(false)
            }
        }.runTaskTimer(plugin, 0, CLEANING_MOB_PERIOD.toLong())
    }

    @EventHandler
    fun onDisable(e: PluginDisableEvent) {
        cleanAutoSummonableMob(true)
    }

    private fun cleanAutoSummonableMob(toAllMobs: Boolean) {
        for (world in activeWorlds) {
            world.entities.stream()
                    .filter { it is Monster && it.hasMetadata(MobSummoner.AUTO_SUMMONED_MOB_METAKEY) }
                    .filter { toAllMobs || ((it as CraftEntity).handle as EntityMonster).goalTarget == null }
                    .forEach { it.remove() }
        }
    }
}