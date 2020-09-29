package kr.sul.worldmanager

import kr.sul.worldmanager.setting.Setting
import kr.sul.worldmanager.summonmob.AutomaticallySummonMob
import org.bukkit.plugin.java.JavaPlugin

class WorldManager : JavaPlugin() {
    companion object {
        lateinit var instance: WorldManager private set
    }

    override fun onEnable() {
        instance = this
        AutomaticallySummonMob(instance)
        Setting(instance)
    }

    override fun onDisable() {}
}