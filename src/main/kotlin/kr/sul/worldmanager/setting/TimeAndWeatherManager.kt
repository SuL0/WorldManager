package kr.sul.worldmanager.setting

import kr.sul.worldmanager.WorldManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent

object TimeAndWeatherManager : Listener {
    @EventHandler
    fun onWeatherChange(e: WeatherChangeEvent) {
        if (e.isCancelled) return
        if (e.toWeatherState()) { // true if the weather is being set to raining, false otherwise
            e.isCancelled = true
        }
    }

    init {
        // 월드 시간 고정 (08:00 ~ 16:00)
        // 06:00 이 0틱. 1시간당 1000틱. 전체 틱은 24000.
        Bukkit.getScheduler().runTaskTimer(WorldManager.instance, {
            for (world in Bukkit.getServer().worlds) {
                val time = world.time
                if (time !in 2000..10000) {
                    world.time = 2000
                }
                if (world.hasStorm()) world.setStorm(false)
                if (world.isThundering) world.isThundering = false
            }
        }, 1L, 40 * 20L)
    }
}