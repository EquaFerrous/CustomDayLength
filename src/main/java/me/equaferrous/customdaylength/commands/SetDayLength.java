package me.equaferrous.customdaylength.commands;

import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetDayLength implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("setdaylength")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED +"/setdaylength <value> <x / ticks>");
                return true;
            }

            double value;
            try {
                value = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED +"Value must be a valid number.");
                return true;
            }

            String option = args[1];
            if (option.equalsIgnoreCase("x")) {
                if (value < 0.1) {
                    sender.sendMessage(ChatColor.RED +"Day length multiplier must be greater or equal to 0.1.");
                    return true;
                }
                value = Math.floor(value * 10) / 10;
                TimeManager.GetInstance().SetDayLengthMultiplier(value);

                int realTime = (int) (20 * value);
                sender.sendMessage(ChatColor.GRAY +"Day length set to "+ value +"x default. (~"+ realTime +" minutes).");
            }

            else if (option.equalsIgnoreCase("ticks")) {
                sender.sendMessage(ChatColor.RED +"Not yet implemented.");
                return true;
            }

            else {
                sender.sendMessage(ChatColor.RED +"Value mode must be 'x' or 'ticks'.");
                return true;
            }

        }

        return true;
    }
}
