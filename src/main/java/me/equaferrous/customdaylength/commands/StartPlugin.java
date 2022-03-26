package me.equaferrous.customdaylength.commands;

import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartPlugin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("startcustomtime")) {
            TimeManager.GetInstance().StartCustomRunning();
            Bukkit.broadcastMessage(ChatColor.GRAY + "[DEBUG] Custom time started");
        }

        return true;
    }
}
