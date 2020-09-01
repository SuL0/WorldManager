package me.sul.worldmanager.summonmob.mobtype;

import com.google.common.base.Enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AutoSummonableMobFactory {
    private static final Random random = new Random();
    private static final Map<MobType, AutoSummonableMob> mobMap = new HashMap<>();

    static {
        mobMap.put(MobType.ZOMBIE, Zombie.getInstance());
        mobMap.put(MobType.SKELETON, Skeleton.getInstance());
    }

    public static AutoSummonableMob getAutoSummonableMob(String str) {
        if (!Enums.getIfPresent(AutoSummonableMobFactory.MobType.class, str).isPresent()) return null;
        MobType mobType = MobType.valueOf(str);
        if (mobMap.get(mobType) != null) {
            return mobMap.get(mobType);
        }
        return null;
    }
    public static AutoSummonableMob getRandomAutoSummonableMob() {
        double rand = random.nextInt(100) + 1;
        if (rand <= 50) {
            return mobMap.get(MobType.ZOMBIE);
        } else {
            return mobMap.get(MobType.SKELETON);
        }
    }

    enum MobType {
        ZOMBIE,
        SKELETON
    }
}
