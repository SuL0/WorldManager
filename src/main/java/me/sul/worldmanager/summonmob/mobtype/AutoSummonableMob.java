package me.sul.worldmanager.summonmob.mobtype;

import org.bukkit.Location;

public interface AutoSummonableMob {
    void summonMob(Location loc);
    double getSpawnChance();
    int getMinDistance();
    int getMaxDistance();
}
