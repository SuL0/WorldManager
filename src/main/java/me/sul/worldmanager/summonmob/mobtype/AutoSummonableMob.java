package me.sul.worldmanager.summonmob.mobtype;

import me.sul.worldmanager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AutoSummonableMob {
    public abstract void summonMobAroundLoc(Location origLoc);

    private static final Random random = new Random();
    private static final List<String> groundBlockIds = new ArrayList<>();


    public static Location getNearAppropriateLoc(Location origLoc, int minDistance, int maxDistance) {
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

    private static final int LOWEST_GROUND_HEIGHT = 70 +1;
    private static final int HIGHEST_GROUND_HEIGHT = 120 +1;


    private static Location getGroundLoc(World world, int x, int z) {
        if (groundBlockIds.size() == 0) {
            groundBlockIds.addAll(WorldManager.getInstance().getConfig().getStringList("summon-mob.ground-block-ids"));
        }

        for (int y=LOWEST_GROUND_HEIGHT; y<HIGHEST_GROUND_HEIGHT; y++) {  // TODO: 맵을 최대한 낮춰서 100을 수정하기
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
