package me.equaferrous.customdaylength;

import me.equaferrous.customdaylength.commands.SetDayLength;
import me.equaferrous.customdaylength.commands.StartPlugin;
import me.equaferrous.customdaylength.commands.StopPlugin;
import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomDayLength extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("startcustomtime").setExecutor(new StartPlugin());
        this.getCommand("stopcustomtime").setExecutor(new StopPlugin());
        this.getCommand("setdaylength").setExecutor(new SetDayLength());

        new TimeManager();

        Bukkit.getServer().getConsoleSender().sendMessage("Custom Day Length -- Enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

        Bukkit.getServer().getConsoleSender().sendMessage("Custom Day Length -- Disabled");
    }
}
