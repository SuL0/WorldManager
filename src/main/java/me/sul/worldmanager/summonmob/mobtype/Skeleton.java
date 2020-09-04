package me.sul.worldmanager.summonmob.mobtype;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class Skeleton implements AutoSummonableMob {
    private final double SUMMONING_CHANCE;
    private final int MIN_DISTANCE;
    private final int MAX_DISTANCE;

    public Skeleton(FileConfiguration config, String parentNode) {
        parentNode = parentNode + ".skeleton";
        SUMMONING_CHANCE = config.getDouble(parentNode + ".summoning-chance");
        MIN_DISTANCE = config.getInt(parentNode + ".min-distance");
        MAX_DISTANCE = config.getInt(parentNode + ".max-distance");
    }

    @Override
    public Entity summonMob(Location loc) {
        Entity entity = loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
        return entity;
    }

    @Override
    public double getSpawnChance() { return SUMMONING_CHANCE; }
    @Override
    public int getMinDistance() { return MIN_DISTANCE; }
    @Override
    public int getMaxDistance() { return MAX_DISTANCE; }
}
