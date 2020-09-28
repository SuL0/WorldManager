package kr.sul.worldmanager.summonmob.mobtype

import com.google.common.base.Enums
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

class AutoSummonableMobFactory(config: FileConfiguration, parentNodeParam: String) {
    companion object {
        private val random = Random()
        private val enumMobMap = EnumMap<MobType, AutoSummonableMob>(MobType::class.java)
    }
    init {
        var parentNode = parentNodeParam
        parentNode = "$parentNode.summoner.mobtype"
        enumMobMap[MobType.ZOMBIE] = Zombie(config, parentNode)
        enumMobMap[MobType.SKELETON] = Skeleton(config, parentNode)
    }

    fun getAutoSummonableMob(str: String): AutoSummonableMob {
        if (!Enums.getIfPresent(MobType::class.java, str).isPresent) throw Exception(str)
        val mobType = MobType.valueOf(str)
        return enumMobMap[mobType] ?: throw Exception()
    }

    val randomAutoSummonableMob: AutoSummonableMob
        get() {
            val rand = (random.nextInt(10000) + 1) / 100.0
            var chance = 0.0
            for (mob in enumMobMap.values) {
                chance += mob.spawnChance
                if (chance > 100) break
                if (chance >= rand) {
                    return mob
                }
            }
            return enumMobMap[MobType.ZOMBIE] ?: throw Exception()
        }

    internal enum class MobType {
        ZOMBIE, SKELETON
    }
}
