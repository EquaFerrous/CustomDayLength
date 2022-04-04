package me.equaferrous.customdaylength.commands;

import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DisableCustomTime implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("disablecustomtime")) {
            if (args.length != 0) {
                sender.sendMessage(ChatColor.RED +"Incorrect command. Correct usage -->\n/DisableCustomTime");
                return true;
            }


            TimeManager.GetInstance().DisableCustomTime();
            sender.sendMessage(ChatColor.GRAY + "Custom Time Plugin disabled.");
        }

        return true;
    }
}
