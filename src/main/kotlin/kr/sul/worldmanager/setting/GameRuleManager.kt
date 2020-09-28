package kr.sul.worldmanager.setting

import kr.sul.worldmanager.WorldManager
import org.bukkit.Bukkit

object GameRuleManager {
    init {
        // 월드 GameRule 설정
        Bukkit.getScheduler().runTaskLater(WorldManager.instance, {  // 월드는 1틱 뒤에 불러와야 함
            for (world in Bukkit.getServer().worlds) {
                if (world.getGameRuleValue("announceAdvancements") == "true") {
                    world.setGameRuleValue("announceAdvancements", "false")
                }
            }
        }, 1L)
    }
}