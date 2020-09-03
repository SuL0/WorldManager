package me.sul.worldmanager.summonmob.mobtype;

import me.sul.customentity.entity.EntityZombie;
import me.sul.worldmanager.WorldManager;
import me.sul.worldmanager.summonmob.MobCleaner;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

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
        Entity entity = new EntityZombie(loc).getBukkitEntity();
        entity.setMetadata(MobCleaner.CLEANING_TARGET_METAKEY, new FixedMetadataValue(WorldManager.getInstance(), true));
    }

    @Override
    public double getSpawnChance() { return SPAWN_CHANGE; }
    @Override
    public int getMinDistance() { return MIN_DISTANCE; }
    @Override
    public int getMaxDistance() { return MAX_DISTANCE; }
}
