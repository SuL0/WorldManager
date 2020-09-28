package kr.sul.worldmanager.summonmob.mobtype

import me.sul.customentity.entity.EntityZombie
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity

class Zombie(config: FileConfiguration, parentNodeParam: String) : AutoSummonableMob {
    override val spawnChance: Double
    override val minDistance: Int
    override val maxDistance: Int
    override fun summonMob(loc: Location): Entity {
        return EntityZombie(loc).bukkitEntity
    }

    init {
        var parentNode = parentNodeParam
        parentNode = "$parentNode.zombie"
        spawnChance = config.getDouble("$parentNode.summoning-chance")
        minDistance = config.getInt("$parentNode.min-distance")
        maxDistance = config.getInt("$parentNode.max-distance")
    }
}