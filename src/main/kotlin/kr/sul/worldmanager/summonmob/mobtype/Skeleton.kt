package kr.sul.worldmanager.summonmob.mobtype

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

class Skeleton(config: FileConfiguration, parentNodeParam: String) : AutoSummonableMob {
    override val spawnChance: Double
    override val minDistance: Int
    override val maxDistance: Int
    override fun summonMob(loc: Location): Entity {
        return loc.world.spawnEntity(loc, EntityType.SKELETON)
    }

    init {
        var parentNode = parentNodeParam
        parentNode = "$parentNode.skeleton"
        spawnChance = config.getDouble("$parentNode.summoning-chance")
        minDistance = config.getInt("$parentNode.min-distance")
        maxDistance = config.getInt("$parentNode.max-distance")
    }
}