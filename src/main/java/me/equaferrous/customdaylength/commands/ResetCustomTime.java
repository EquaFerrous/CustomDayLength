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
            if (args.length != 0) {
                sender.sendMessage(ChatColor.RED +"Incorrect command. Correct usage -->\n/ResetCustomTime");
                return true;
            }


            TimeManager timeManager = TimeManager.GetInstance();

            timeManager.ResetLengths();
            sender.sendMessage(ChatColor.GRAY + "Custom Time Plugin reset.");
            timeManager.PrintTimeInfo(sender);
        }

        return true;
    }
}
