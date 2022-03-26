package me.equaferrous.customdaylength;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomDayLength extends JavaPlugin {

    @Override
    public void onEnable() {


        Bukkit.getServer().getConsoleSender().sendMessage("Custom Day Length -- Enabled");
    }

    @Override
    public void onDisable() {


        Bukkit.getServer().getConsoleSender().sendMessage("Custom Day Length -- Disabled");
    }
}
