package me.sul.worldmanager.summonmob;

import me.sul.worldmanager.WorldManager;
import me.sul.worldmanager.summonmob.mobtype.AutoSummonableMobFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: 플레이어 시선 앞쪽에 스폰될 확률을 50%정도로
public class AutomaticallySummonMob {
    private final Plugin plugin;

    private final AutoSummonableMobFactory autoSummonableMobFactory;
    private final MobSummoner mobSummoner;


    // config
    private final List<World> activeWorlds = new ArrayList<>();
    private final int mobSpawnPeriod;
    private final int maxCompanionDistance;
    private final int maxCompanionNum;


    public AutomaticallySummonMob() {
        this.plugin = WorldManager.getInstance();

        plugin.saveDefaultConfig(); // config가 없을 시 생성
        FileConfiguration config = plugin.getConfig();
        String parentNode = "summon-mob";
        mobSpawnPeriod = config.getInt(parentNode + ".mob-spawn-period");
        maxCompanionDistance = config.getInt(parentNode + ".max-companion-distance");
        maxCompanionNum = config.getInt(parentNode + ".max-companion-num");

        autoSummonableMobFactory = new AutoSummonableMobFactory(config, parentNode);
        mobSummoner = new MobSummoner(config, parentNode);

        // 월드는 1틱 뒤에 불러와야 함
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            activeWorlds.addAll(config.getStringList(parentNode + ".active-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()));
            if (activeWorlds.size() >= 1) {
                registerSummonMobScheduler();
                new MobCleaner(plugin, activeWorlds);
            }
        }, 1L);
    }




    private void registerSummonMobScheduler() {
        new BukkitRunnable() {
            // TODO: 한꺼번에 소환하지 않고 차차 소환하도록 변경
            @Override
            public void run() {
                List<Location> centerPoints = getCenterPointsToSummonMobs();
                if (centerPoints == null || centerPoints.size() == 0) return;
                for (Location centerPoint : centerPoints) {
                    mobSummoner.summonMob(autoSummonableMobFactory.getRandomAutoSummonableMob(), centerPoint);
                }
            }
        }.runTaskTimer(plugin, mobSpawnPeriod, mobSpawnPeriod);
    }


    private List<Location> getCenterPointsToSummonMobs() {
        List<Player> players = new ArrayList<>();
        for (World world : activeWorlds) {
            if (world.getPlayers() == null) continue;
            players.addAll(world.getPlayers());
        }
        if (players.size() == 0) return null;
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
