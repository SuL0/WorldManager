package me.sul.worldmanager.summonmob.mobtype;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface AutoSummonableMob {
    Entity summonMob(Location loc);
    double getSpawnChance();
    int getMinDistance();
    int getMaxDistance();
}
