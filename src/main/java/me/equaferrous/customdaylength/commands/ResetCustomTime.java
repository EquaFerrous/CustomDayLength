package me.equaferrous.customdaylength.commands;

import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetCustomTime implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("resetcustomtime")) {
            TimeManager.GetInstance().ResetLengths();

            sender.sendMessage(ChatColor.GRAY + "Custom Time Plugin reset.");
            TimeManager.GetInstance().PrintTimeInfo(sender);
        }
        return true;
    }
}
