package kr.sul.worldmanager.summonmob

import kr.sul.worldmanager.summonmob.mobtype.AutoSummonableMobFactory
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.stream.Collectors

// TODO: 플레이어 시선 앞쪽에 스폰될 확률을 50%정도로
class AutomaticallySummonMob(private val plugin: Plugin) {
    private val autoSummonableMobFactory: AutoSummonableMobFactory
    private val mobSummoner: MobSummoner

    // config
    private val activeWorlds: MutableList<World> = ArrayList()
    private val summoningMobPeriod: Int
    private val maxCompanionDistance: Int
    private val maxCompanionNum: Int
    private val mobLimit_radius: Int
    private val mobLimit_maxNum: Int

    init {
        plugin.saveDefaultConfig() // config가 없을 시 생성
        val config = plugin.config
        val parentNode = "summon-mob"
        summoningMobPeriod = config.getInt("$parentNode.summoning-mob-period") * 20
        maxCompanionDistance = config.getInt("$parentNode.max-companion-distance")
        maxCompanionNum = config.getInt("$parentNode.max-companion-num")
        mobLimit_radius = config.getInt("$parentNode.moblimit-radius")
        mobLimit_maxNum = config.getInt("$parentNode.moblimit-max-num")
        autoSummonableMobFactory = AutoSummonableMobFactory(config, parentNode)
        mobSummoner = MobSummoner(config, parentNode)
        Bukkit.getScheduler().runTaskLater(plugin, {
            // 월드는 1틱 뒤에 불러와야 함
            activeWorlds.addAll(config.getStringList("$parentNode.active-worlds").stream()
                    .map { name: String? -> Bukkit.getWorld(name) }
                    .filter { obj: World? -> Objects.nonNull(obj) }.collect(Collectors.toList()))
            if (activeWorlds.size >= 1) {
                registerSummonMobScheduler()
                MobCleaner(plugin, parentNode, activeWorlds)
            }
        }, 1L)
    }


    private fun registerSummonMobScheduler() {
        object : BukkitRunnable() {
            // TODO: 한꺼번에 소환하지 않고 차차 소환하도록 변경
            override fun run() {
                val centerPoints = getCenterPointsToSummonMobs()
                if (centerPoints == null || centerPoints.isEmpty()) return
                for (centerPoint in centerPoints) {
                    mobSummoner.summonMob(autoSummonableMobFactory.randomAutoSummonableMob, centerPoint)
                }
            }
        }.runTaskTimer(plugin, summoningMobPeriod.toLong(), summoningMobPeriod.toLong())
    }
    private fun getCenterPointsToSummonMobs(): List<Location>? {
        val players = ArrayList<Player>()
        for (world in activeWorlds) {
            if (world.players == null) continue
            players.addAll(world.players.filter { p: Player -> p.gameMode == GameMode.SURVIVAL })
        }
        if (players.size == 0) return null
        removePlayerExceededMobLimit(players, mobLimit_radius, mobLimit_maxNum)
        removeCompanion(players, maxCompanionDistance, maxCompanionNum)
        return players.map { it.location }
    }

    private fun removePlayerExceededMobLimit(playerList: ArrayList<Player>, radius: Int, maxNum: Int) {
        var i = 0
        while (i < playerList.size) {
            val loopPlayer = playerList[i]
            if (loopPlayer.getNearbyEntities(radius.toDouble(), 1000.0, radius.toDouble()).stream().filter { e: Entity -> e.hasMetadata(MobSummoner.Companion.AUTO_SUMMONED_MOB_METAKEY) }.count() >= maxNum) {
                playerList.removeAt(i--) // 인덱스가 앞으로 당겨지니까, 1을 빼줘야 함
            }
            i++
        }
    }

    private fun removeCompanion(playerList: ArrayList<Player>, maxDistance: Int, maxCompanionNum: Int) {
        playerList.shuffle()
        for (i in playerList.indices) {
            val loopPlayer = playerList[i]
            var companionNum = 1
            // NOTE: getNearbyEntities는 자신 포함인가? 그럼 아래 코드 자신은 제외하도록 수정해야하는데
            for (nearPlayer in loopPlayer.getNearbyEntities(maxDistance.toDouble(), maxDistance.toDouble(), maxDistance.toDouble())
                    .filter { it is Player && playerList.contains(it) }) {
                playerList.remove(nearPlayer) // 무조건 nearEntity는 playerList에서 loopPlayer보다 뒤에 있음.
                if (++companionNum >= maxCompanionNum) {
                    break
                }
            }
        }
    }
}