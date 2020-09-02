package me.sul.worldmanager.summonmob.mobtype;

import me.sul.customentity.entity.EntityZombie;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Zombie implements AutoSummonableMob {
    private final double SPAWN_CHANGE;
    private final int MIN_DISTANCE;
    private final int MAX_DISTANCE;

    public Zombie(FileConfiguration config, String parentNode) {
        parentNode = parentNode + ".zombie";
        SPAWN_CHANGE = config.getDouble(parentNode + ".spawn-chance");
        MIN_DISTANCE = config.getInt(parentNode + ".min-distance");
        MAX_DISTANCE = config.getInt(parentNode + ".max-distance");
    }

    @Override
    public void summonMob(Location loc) {
        new EntityZombie(loc);
    }

    @Override
    public double getSpawnChance() { return SPAWN_CHANGE; }
    @Override
    public int getMinDistance() { return MIN_DISTANCE; }
    @Override
    public int getMaxDistance() { return MAX_DISTANCE; }
}
