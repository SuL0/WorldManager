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

    private final List<World> activeWorlds = new ArrayList<>();
    private final int CLEAN_MOB_PERIOD;


    public MobCleaner(Plugin plugin, String parentNode, List<World> activeWorlds) {
        this.plugin = plugin;
        this.activeWorlds.addAll(activeWorlds);
        this.CLEAN_MOB_PERIOD = plugin.getConfig().getInt(parentNode + ".cleaning-mob-period") * 20;

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
                if (entity instanceof Monster && entity.hasMetadata(MobSummoner.AUTO_SUMMONED_MOB_METAKEY)) {
                    if (includeUsefulMobs || ((EntityMonster) ((CraftEntity)entity).getHandle()).getGoalTarget() == null) {
                        entity.remove();
                    }
                }
            }
        }
    }
}
