package me.sul.worldmanager.summonmob.mobtype;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

public class Zombie implements AutoSummonableMob {
    private final int SPAWN_CHANGE;
    private final int MIN_DISTANCE;
    private final int MAX_DISTANCE;

    public Zombie(FileConfiguration config, String parentNode) {
        parentNode = parentNode + ".zombie";
        SPAWN_CHANGE = config.getInt(parentNode + ".spawn-chance");
        MIN_DISTANCE = config.getInt(parentNode + ".min-distance");
        MAX_DISTANCE = config.getInt(parentNode + ".max-distance");
    }
    
    @Override
    public void summonMob(Location loc) {
        loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
    }

    @Override
    public int getSpawnChance() { return SPAWN_CHANGE; }
    @Override
    public int getMinDistance() { return MIN_DISTANCE; }
    @Override
    public int getMaxDistance() { return MAX_DISTANCE; }
}
