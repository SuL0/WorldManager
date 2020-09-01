package me.sul.worldmanager;

import me.sul.worldmanager.summonmob.AutomaticallySummonMob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldManager extends JavaPlugin {
    private static WorldManager instance;

    @Override
    public void onEnable() {
        instance = this;
        new AutomaticallySummonMob();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }

    public static WorldManager getInstance() { return instance; }
}
