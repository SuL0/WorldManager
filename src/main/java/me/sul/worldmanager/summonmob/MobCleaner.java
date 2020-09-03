package me.sul.worldmanager.summonmob;

import net.minecraft.server.v1_12_R1.EntityMonster;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MobCleaner implements Listener {
    private final Plugin plugin;

    private final List<World> activeWorlds = new ArrayList<World>();
    private static final int CLEAN_MOB_PERIOD = 60 * 20;
    public static final String CLEANING_TARGET_METAKEY = "WorldManager.CleaningTarget";


    public MobCleaner(Plugin plugin, List<World> activeWorlds) {
        this.plugin = plugin;
        this.activeWorlds.addAll(activeWorlds);
        registerCleanUselessMobScheduler();
    }
    private void registerCleanUselessMobScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanAutoSummonableMob(false);
            }
        }.runTaskTimer(plugin, 0, CLEAN_MOB_PERIOD);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        cleanAutoSummonableMob(true);
    }

    private void cleanAutoSummonableMob(boolean includeUsefulMobs) {
        for (World world : activeWorlds) {
            // mobType은 Skeleton, Zombie여야 함
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Monster && entity.hasMetadata(CLEANING_TARGET_METAKEY)) {
                    if (!includeUsefulMobs || ((EntityMonster) ((CraftEntity)entity).getHandle()).getGoalTarget() == null) {
                        entity.remove();
                    }
                }
            }
            world.getEntities().stream().filter(e -> e instanceof Monster && ((EntityMonster) ((CraftEntity)e).getHandle()).getGoalTarget() == null).forEach(Entity::remove);
        }
    }
}
