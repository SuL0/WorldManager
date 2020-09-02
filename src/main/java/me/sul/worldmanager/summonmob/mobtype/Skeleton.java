package me.sul.worldmanager.summonmob.mobtype;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

public class Skeleton implements AutoSummonableMob {
    private final double SPAWN_CHANGE;
    private final int MIN_DISTANCE;
    private final int MAX_DISTANCE;

    public Skeleton(FileConfiguration config, String parentNode) {
        parentNode = parentNode + ".skeleton";
        SPAWN_CHANGE = config.getDouble(parentNode + ".spawn-chance");
        MIN_DISTANCE = config.getInt(parentNode + ".min-distance");
        MAX_DISTANCE = config.getInt(parentNode + ".max-distance");
    }

    @Override
    public void summonMob(Location loc) {
        loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
    }

    @Override
    public double getSpawnChance() { return SPAWN_CHANGE; }
    @Override
    public int getMinDistance() { return MIN_DISTANCE; }
    @Override
    public int getMaxDistance() { return MAX_DISTANCE; }
}
