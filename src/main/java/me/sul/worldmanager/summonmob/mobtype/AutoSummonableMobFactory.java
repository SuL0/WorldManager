package me.sul.worldmanager.summonmob.mobtype;

import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AutoSummonableMobFactory {
    private static final Random random = new Random();
    private final Map<MobType, AutoSummonableMob> mobMap = new HashMap<>();

    enum MobType {
        ZOMBIE,
        SKELETON
    }

    public AutoSummonableMobFactory(FileConfiguration config, String parentNode) {
        parentNode = parentNode + ".summoner.mobtype";
        mobMap.put(MobType.ZOMBIE, new Zombie(config, parentNode));
        mobMap.put(MobType.SKELETON, new Skeleton(config, parentNode));
    }

    public AutoSummonableMob getAutoSummonableMob(String str) {
        if (!Enums.getIfPresent(AutoSummonableMobFactory.MobType.class, str).isPresent()) return null;
        MobType mobType = MobType.valueOf(str);
        if (mobMap.get(mobType) != null) {
            return mobMap.get(mobType);
        }
        return null;
    }
    public AutoSummonableMob getRandomAutoSummonableMob() {
        double rand = (random.nextInt(10000) + 1 ) / 100D;
        double chance = 0;
        for (AutoSummonableMob mob : mobMap.values()) {
            chance += mob.getSpawnChance();
            if (chance > 100) break;
            if (chance >= rand) {
                return mob;
            }
        }
        return mobMap.get(MobType.ZOMBIE);
    }
}
