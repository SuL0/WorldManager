package me.sul.worldmanager.summonmob;

import me.sul.worldmanager.WorldManager;
import me.sul.worldmanager.summonmob.mobtype.AutoSummonableMobFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

// TODO: 플레이어 시선 앞쪽에 스폰될 확률을 50%정도로
public class AutomaticallySummonMob {
    private final Plugin plugin;
    private final Random random = new Random();

    // config
    private final List<World> activeWorlds = new ArrayList<>();
    private final int maxCompanionDistance;
    private final int maxCompanionNum;


    public AutomaticallySummonMob() {
        this.plugin = WorldManager.getInstance();

        plugin.saveDefaultConfig(); // config가 없을 시 생성
        activeWorlds.addAll(plugin.getConfig().getStringList("summon-mob.active-worlds").stream().map(Bukkit::getWorld).collect(Collectors.toList()));
        maxCompanionDistance = plugin.getConfig().getInt("summon-mob.max-companion-distance");
        maxCompanionNum = plugin.getConfig().getInt("summon-mob.max-companion-num");
        if (activeWorlds.size() >= 1) {
            registerSpawnZombieScheduler();
        }
    }

    private void registerSpawnZombieScheduler() {
        new BukkitRunnable() {
            // TODO: runTaskTimer 추가
            @Override
            public void run() {
                List<Location> centerPoints = getCenterPointsToSummonMobs();
                if (centerPoints == null || centerPoints.size() == 0) return;
                for (Location centerPoint : centerPoints) {
                    AutoSummonableMobFactory.getRandomAutoSummonableMob().summonMobAroundLoc(centerPoint);
                }
            }
        }.runTaskTimer(plugin, 0, 50 + random.nextInt(50));
    }


    
    private List<Location> getCenterPointsToSummonMobs() {
        List<Player> players = new ArrayList<>();
        for(World world : activeWorlds) {
            players.addAll(world.getPlayers());
        }
        return removeCompanionFromList(players, maxCompanionDistance, maxCompanionNum).stream().map(Entity::getLocation).collect(Collectors.toList());
    }

    private List<Player> removeCompanionFromList(List<Player> playerList, int maxDistance, int maxCompanionNum) {
        Collections.shuffle(playerList);

        for (int i=0; i<playerList.size(); i++) {
            Player loopPlayer = playerList.get(i);
            int companionNum = 1;
            for (Entity nearEntity : loopPlayer.getNearbyEntities(maxDistance, maxDistance, maxDistance).stream().filter(nearEntity -> (nearEntity instanceof Player && playerList.contains(nearEntity))).collect(Collectors.toList())) {
                playerList.remove(nearEntity); //NOTE: Suspicious call ? ? ?  // 무조건 nearEntity는 playerList에서 loopPlayer보다 뒤에 있음.
                if (++companionNum >= maxCompanionNum) {
                    break;
                }
            }
        }
        return playerList;
    }
}
