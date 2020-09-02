package me.sul.worldmanager.summonmob;

import me.sul.worldmanager.summonmob.mobtype.AutoSummonableMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobSummoner {
    private static final Random random = new Random();
    private final List<String> groundBlockIds = new ArrayList<>();
    private final int LOWEST_GROUND_HEIGHT;
    private final int HIGHEST_GROUND_HEIGHT;

    public MobSummoner(FileConfiguration config, String parentNode) {
        parentNode = parentNode + ".summoner";
        groundBlockIds.addAll(config.getStringList(parentNode + ".ground-block-ids"));
        LOWEST_GROUND_HEIGHT = config.getInt(parentNode + ".lowest-ground-height");
        HIGHEST_GROUND_HEIGHT = config.getInt(parentNode + ".highest-ground-height");
    }


    public void summonMob(AutoSummonableMob mob, Location centerPoint) {
        int minDistance = mob.getMinDistance();
        int maxDistance = mob.getMaxDistance();
        Location summonLoc = getNearAppropriateLoc(centerPoint, minDistance, maxDistance);
        if (summonLoc == null) return;
        mob.summonMob(summonLoc);
    }

    public Location getNearAppropriateLoc(Location origLoc, int minDistance, int maxDistance) {
        for (int retry=0; retry<10; retry++) {
            int randX = (int) origLoc.getX() + Math.round(random.nextBoolean() ? 1 : -1 * (minDistance + random.nextInt(maxDistance)));
            int randZ = (int) origLoc.getZ() + Math.round(random.nextBoolean() ? 1 : -1 * (minDistance + random.nextInt(maxDistance)));
            Location groundLoc = getGroundLoc(origLoc.getWorld(), randX, randZ);
            if (groundLoc != null) {
                return groundLoc;
            }
        }
        return null;
    }
    private Location getGroundLoc(World world, int x, int z) {
        for (int y=LOWEST_GROUND_HEIGHT; y<HIGHEST_GROUND_HEIGHT; y++) {
            if (world.getBlockAt(x, y, z).getType() == Material.AIR) {
                int tempY = y-1;
                Block blockJustBelow = world.getBlockAt(x, tempY, z);
                String blockJustBelowId = blockJustBelow.getTypeId() + ":" + blockJustBelow.getData(); // 한칸 밑 블럭
                if (groundBlockIds.contains(blockJustBelowId)) {
                    return blockJustBelow.getLocation().add(0, 1, 0);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}