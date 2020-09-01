package me.sul.worldmanager.summonmob.mobtype;

import me.sul.worldmanager.WorldManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class Skeleton extends AutoSummonableMob {
    private static AutoSummonableMob instance;

    private final int minDistance;
    private final int maxDistance;

    public Skeleton() {
        Plugin plugin = WorldManager.getInstance();
        int minDistance = plugin.getConfig().getInt("summon-mob." + getClass().getSimpleName().toLowerCase() + ".min-distance");
        int maxDistance = plugin.getConfig().getInt("summon-mob." + getClass().getSimpleName().toLowerCase() + ".max-distance");
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }


    @Override
    public void summonMobAroundLoc(Location origLoc) {
        Location summonLoc = getNearAppropriateLoc(origLoc, minDistance, maxDistance);
        if (summonLoc != null) {
            summonLoc.getWorld().spawnEntity(summonLoc, EntityType.SKELETON);
            // 소환 모션 코드
        }
    }

    public static AutoSummonableMob getInstance() {
        if (instance == null) {
            instance = new Skeleton();
        }
        return instance;
    }
}
