package kr.sul.worldmanager.setting

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

object MobDropsManager : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onDeath(e: EntityDeathEvent) {
        if (e.entity !is Player) {
            e.drops.clear()
        }
        e.droppedExp = 0
    }
}