package me.sul.worldmanager.summonmob;

import me.sul.worldmanager.WorldManager;
import me.sul.worldmanager.summonmob.mobtype.AutoSummonableMobFactory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
    private final int summoningMobPeriod;
    private final int maxCompanionDistance;
    private final int maxCompanionNum;
    private final int mobLimit_radius;
    private final int mobLimit_maxNum;


    public AutomaticallySummonMob() {
        this.plugin = WorldManager.getInstance();

        plugin.saveDefaultConfig(); // config가 없을 시 생성
        FileConfiguration config = plugin.getConfig();
        String parentNode = "summon-mob";
        summoningMobPeriod = config.getInt(parentNode + ".summoning-mob-period") * 20;
        maxCompanionDistance = config.getInt(parentNode + ".max-companion-distance");
        maxCompanionNum = config.getInt(parentNode + ".max-companion-num");
        mobLimit_radius = config.getInt(parentNode + ".moblimit-radius");
        mobLimit_maxNum = config.getInt(parentNode + ".moblimit-max-num");

        autoSummonableMobFactory = new AutoSummonableMobFactory(config, parentNode);
        mobSummoner = new MobSummoner(config, parentNode);

        // 월드는 1틱 뒤에 불러와야 함
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            activeWorlds.addAll(config.getStringList(parentNode + ".active-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()));
            if (activeWorlds.size() >= 1) {
                registerSummonMobScheduler();
                new MobCleaner(plugin, parentNode, activeWorlds);
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
        }.runTaskTimer(plugin, summoningMobPeriod, summoningMobPeriod);
    }


    private List<Location> getCenterPointsToSummonMobs() {
        List<Player> players = new ArrayList<>();
        for (World world : activeWorlds) {
            if (world.getPlayers() == null) continue;
            players.addAll(world.getPlayers().stream().filter(p -> p.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList()));
        }
        if (players.size() == 0) return null;
        removePlayerExceededMobLimit(players, mobLimit_radius, mobLimit_maxNum);
        removeCompanion(players, maxCompanionDistance, maxCompanionNum);
        return players.stream().map(Entity::getLocation).collect(Collectors.toList());
    }

    private void removePlayerExceededMobLimit(List<Player> playerList, int radius, int maxNum) {
        for (int i=0; i<playerList.size(); i++) {
            Player loopPlayer = playerList.get(i);
            if (loopPlayer.getNearbyEntities(radius, 1000, radius).stream().filter(e -> e.hasMetadata(MobSummoner.AUTO_SUMMONED_MOB_METAKEY)).count() >= maxNum) {
                playerList.remove(i--); // 인덱스가 앞으로 당겨지니까, 1을 빼줘야 함
            }
        }
    }

    private void removeCompanion(List<Player> playerList, int maxDistance, int maxCompanionNum) {
        Collections.shuffle(playerList);

        for (int i=0; i<playerList.size(); i++) {
            Player loopPlayer = playerList.get(i);
            int companionNum = 1;
            // NOTE: getNearbyEntities는 자신 포함인가? 그럼 아래 코드 자신은 제외하도록 수정해야하는데
            for (Entity nearPlayer : loopPlayer.getNearbyEntities(maxDistance, maxDistance, maxDistance).stream().filter(nearEntity -> (nearEntity instanceof Player && playerList.contains(nearEntity))).collect(Collectors.toList())) {
                playerList.remove(nearPlayer); //NOTE: Suspicious call ? ? ?  // 무조건 nearEntity는 playerList에서 loopPlayer보다 뒤에 있음.
                if (++companionNum >= maxCompanionNum) {
                    break;
                }
            }
        }
    }
}
