package kr.sul.worldmanager.summonmob

import kr.sul.worldmanager.WorldManager
import kr.sul.worldmanager.summonmob.mobtype.AutoSummonableMob
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import kotlin.math.roundToInt

class MobSummoner(config: FileConfiguration, parentNodeParam: String) {
    companion object {
        private val random = Random()
        const val AUTO_SUMMONED_MOB_METAKEY = "WorldManager.AutoSummonedMob"
        private val groundBlockIds: MutableList<String> = ArrayList()
        private var LOWEST_GROUND_HEIGHT: Int = 999999999
        private var HIGHEST_GROUND_HEIGHT: Int = 999999999
    }

    init {
        var parentNode = parentNodeParam
        parentNode = "$parentNode.summoner"
        groundBlockIds.addAll(config.getStringList("$parentNode.ground-block-ids"))
        LOWEST_GROUND_HEIGHT = config.getInt("$parentNode.lowest-ground-height")
        HIGHEST_GROUND_HEIGHT = config.getInt("$parentNode.highest-ground-height")
    }

    fun summonMob(mob: AutoSummonableMob, centerPoint: Location) {
        val minDistance = mob.minDistance
        val maxDistance = mob.maxDistance
        val summonLoc = getNearAppropriateLoc(centerPoint, minDistance, maxDistance) ?: return
        val summonedMob = mob.summonMob(summonLoc)
        summonedMob.setMetadata(AUTO_SUMMONED_MOB_METAKEY, FixedMetadataValue(WorldManager.instance, true))
    }

    private fun getNearAppropriateLoc(origLoc: Location, minDistance: Int, maxDistance: Int): Location? {
        for (retry in 0..9) {
            var randX = origLoc.x.roundToInt()
            var randZ = origLoc.z.roundToInt()
            randX += (if (random.nextBoolean()) 1 else -1) * (minDistance + random.nextInt(maxDistance - minDistance) + 1)
            randZ += (if (random.nextBoolean()) 1 else -1) * (minDistance + random.nextInt(maxDistance - minDistance) + 1)
            val groundLoc = getGroundLoc(origLoc.world, randX, randZ)
            if (groundLoc != null) {
                return groundLoc
            }
        }
        return null
    }

    private fun getGroundLoc(world: World, x: Int, z: Int): Location? {
        for (y in LOWEST_GROUND_HEIGHT..HIGHEST_GROUND_HEIGHT) {
            if (world.getBlockAt(x, y, z).type == Material.AIR) {
                val tempY = y - 1
                val blockJustBelow = world.getBlockAt(x, tempY, z)
                val blockJustBelowId = blockJustBelow.typeId.toString() + ":" + blockJustBelow.data // 한칸 밑 블럭
                return if (groundBlockIds.contains(blockJustBelowId)) {
                    blockJustBelow.location.add(0.0, 1.0, 0.0)
                } else null
            }
        }
        return null
    }
}