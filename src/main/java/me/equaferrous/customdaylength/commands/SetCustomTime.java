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
                sender.sendMessage(ChatColor.RED +"Incorrect command. Correct usage -->\n/SetCustomTime (day | night | full) <minutes>");
                return true;
            }

            double value;
            try {
                value = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED +"Inputted value for <minutes> must be a valid number.");
                return true;
            }

            if (GetDecimalPlaces(value) > 2) {
                sender.sendMessage(ChatColor.RED +"Inputted value for <minutes> can have no more than 2 decimal places.");
                return true;
            }

            
            TimeManager timeManager = TimeManager.GetInstance();
            String option = args[0];
            if (option.equalsIgnoreCase("full")) {
                if (value < 0.1) {
                    sender.sendMessage(ChatColor.RED +"Full day length can not be less than 0.1 minutes (6 seconds).");
                    return true;
                }

                timeManager.SetFullDayLength(value);
                sender.sendMessage(ChatColor.GRAY +"Set custom full-day length.");
                timeManager.PrintTimeInfo(sender);
            }

            else if (option.equalsIgnoreCase("day")) {
                if (value < 0.05) {
                    sender.sendMessage(ChatColor.RED +"Day length can not be less than 0.05 minutes (3 seconds).");
                    return true;
                }

                timeManager.SetDayLength(value);
                sender.sendMessage(ChatColor.GRAY +"Set custom day length.");
                timeManager.PrintTimeInfo(sender);
            }

            else if (option.equalsIgnoreCase("night")) {
                if (value < 0.05) {
                    sender.sendMessage(ChatColor.RED +"Night length can not be less than 0.05 minutes (3 seconds).");
                    return true;
                }

                timeManager.SetNightLength(value);
                sender.sendMessage(ChatColor.GRAY +"Set custom night length.");
                timeManager.PrintTimeInfo(sender);
            }

            else {
                sender.sendMessage(ChatColor.RED +"Must choose an option from (day|night|full).");
            }

        }
        return true;
    }


    // Returns the number of decimal places of a number
    private int GetDecimalPlaces(double decimal) {
        String decimalText = Double.toString(Math.abs(decimal));
        String[] splitText = decimalText.split("\\.");
        return splitText[1].length();
    }

}
