package me.equaferrous.customdaylength;

import me.equaferrous.customdaylength.commands.ResetCustomTime;
import me.equaferrous.customdaylength.commands.SetCustomTime;
import me.equaferrous.customdaylength.commands.EnableCustomTime;
import me.equaferrous.customdaylength.commands.DisableCustomTime;
import me.equaferrous.customdaylength.events.TimeSkip;
import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomDayLength extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("enablecustomtime").setExecutor(new EnableCustomTime());
        this.getCommand("disablecustomtime").setExecutor(new DisableCustomTime());
        this.getCommand("setcustomtime").setExecutor(new SetCustomTime());
        this.getCommand("resetcustomtime").setExecutor(new ResetCustomTime());

        getServer().getPluginManager().registerEvents(new TimeSkip(), this);

        new TimeManager();

        Bukkit.getServer().getConsoleSender().sendMessage("Custom Day Length -- Enabled");
    }

    @Override
    public void onDisable() {
        TimeManager.GetInstance().DisableCustomTime();

        Bukkit.getServer().getConsoleSender().sendMessage("Custom Day Length -- Disabled");
    }
}
