package kr.sul.worldmanager.setting

import kr.sul.servercore.util.ObjectInitializer
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class Setting(plugin: Plugin) {
    init {
        Bukkit.getPluginManager().registerEvents(TimeAndWeatherManager, plugin)
        ObjectInitializer.forceInit(GameRuleManager::class.java)
        Bukkit.getPluginManager().registerEvents(MobDropsManager, plugin)
    }
}