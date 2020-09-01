package me.sul.worldmanager.summonmob.mobtype;

import me.sul.worldmanager.WorldManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class Zombie extends AutoSummonableMob {
    private static Zombie instance;

    private final int minDistance;
    private final int maxDistance;

    public Zombie() {
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
            summonLoc.getWorld().spawnEntity(summonLoc, EntityType.ZOMBIE);
            // 소환 모션 코드
        }
    }

    public static Zombie getInstance() {  // static에서 객체 생성하면, plugin을 받아올 수 없음.
        if (instance == null) {
            instance = new Zombie();
        }
        return instance;
    }
}
