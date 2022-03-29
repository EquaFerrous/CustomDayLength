package me.equaferrous.customdaylength.commands;

import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetCustomTime implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("setcustomtime")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED +"/setcustomtime <day/night/full> <minutes>");
                return true;
            }

            double value;
            try {
                value = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED +"Inputted value for <minutes> must be a valid number.");
                return true;
            }

            String option = args[0];
            if (option.equalsIgnoreCase("full")) {
                if (value < 1) {
                    sender.sendMessage(ChatColor.RED +"Full day length can not be less than 1 minute.");
                    return true;
                }
                value = Math.floor(value * 100) / 100;
                TimeManager.GetInstance().SetFullDayLength(value);

                sender.sendMessage(ChatColor.GRAY +"Set custom full-day length.");
                TimeManager.GetInstance().PrintTimeInfo(sender);
            }

            else if (option.equalsIgnoreCase("day")) {
                if (value < 0.5) {
                    sender.sendMessage(ChatColor.RED +"Day length can not be less than 0.5 minutes (30 seconds).");
                    return true;
                }
                value = Math.floor(value * 100) / 100;
                TimeManager.GetInstance().SetDayLength(value);

                sender.sendMessage(ChatColor.GRAY +"Set custom day length.");
                TimeManager.GetInstance().PrintTimeInfo(sender);
            }

            else if (option.equalsIgnoreCase("night")) {
                if (value < 0.5) {
                    sender.sendMessage(ChatColor.RED +"Night length can not be less than 0.5 minutes (30 seconds).");
                    return true;
                }
                value = Math.floor(value * 100) / 100;
                TimeManager.GetInstance().SetNightLength(value);

                sender.sendMessage(ChatColor.GRAY +"Set custom night length.");
                TimeManager.GetInstance().PrintTimeInfo(sender);
            }

            else {
                sender.sendMessage(ChatColor.RED +"Options for command are 'day' or 'night' or 'full'.");
            }

        }
        return true;
    }

}
