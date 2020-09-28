package kr.sul.worldmanager.summonmob.mobtype

import org.bukkit.Location
import org.bukkit.entity.Entity

interface AutoSummonableMob {
    fun summonMob(loc: Location): Entity
    val spawnChance: Double
    val minDistance: Int
    val maxDistance: Int
}