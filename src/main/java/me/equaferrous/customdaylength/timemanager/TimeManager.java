package me.equaferrous.customdaylength.timemanager;

import me.equaferrous.customdaylength.CustomDayLength;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigInteger;

public class TimeManager {

    private static TimeManager instance;

    private int dayAdvanceAmount;
    private int dayAdvanceDelay;
    private int nightAdvanceAmount;
    private int nightAdvanceDelay;

    private final int defaultDayLength = 14000;
    private final int defaultNightLength = 10000;
    private int customDayLength;
    private int customNightLength;

    private final World overworld;
    private BukkitTask currentTask;
    private boolean enabled;
    private boolean nightValues;

    // ----------------------------------------------

    public TimeManager() {
        instance = this;

        overworld = Bukkit.getWorlds().get(0);
        enabled = false;
        ResetLengths();
    }

    // ------------------------------------------------

    public static TimeManager GetInstance() {
        return instance;
    }

    // -------------------------------------------------

    // Enables the use of custom time
    public void EnableCustomTime() {
        enabled = true;
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        StartCustomTime();
    }

    // Disables the use of custom time
    public void DisableCustomTime() {
        enabled = false;
        StopCurrentTask();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
    }

    // Sets a new day length
    public void SetDayLength(double minutes) {
        customDayLength = (int) (minutes * 60 * 20);
        UpdateDayValues();

        StartCustomTime();
    }

    // Sets a new night length
    public void SetNightLength(double minutes) {
        customNightLength = (int) (minutes * 60 * 20);
        UpdateNightValues();

        StartCustomTime();
    }

    // Sets a new full-day length
    public void SetFullDayLength(double minutes) {
        double dayRatio = (double) defaultDayLength / 24000;
        double nightRatio = (double) defaultNightLength / 24000;
        customDayLength = (int) ((minutes * 60 * 20) * dayRatio);
        customNightLength = (int) ((minutes * 60 * 20) * nightRatio);
        Bukkit.broadcastMessage(String.valueOf(customDayLength));
        Bukkit.broadcastMessage(String.valueOf(customNightLength));
        UpdateDayValues();
        UpdateNightValues();

        StartCustomTime();
    }

    // Resets the day / night length to default
    public void ResetLengths() {
        customDayLength = defaultDayLength;
        customNightLength = defaultNightLength;
        UpdateDayValues();
        UpdateNightValues();

        StartCustomTime();
    }

    // Prints info text based of the current time values
    public void PrintTimeInfo(CommandSender recipient) {
        String text = "";
        double dayLength = ((double) customDayLength / 20) / 60;
        //double standardDay = ((double) defaultDayLength / 20) / 60;
        double nightLength = ((double) customNightLength / 20) / 60;
        //double standardNight = ((double) defaultNightLength / 20) / 60;

        text += "[ Day length : "+ GetMinutesSeconds(dayLength) +" ]";
        text += "\n[ Night length : "+ GetMinutesSeconds(nightLength) +" ]";

        recipient.sendMessage(ChatColor.GRAY + text);
    }

    // ------------------------------------------------------

    // Starts the custom time script
    private void StartCustomTime() {
        if (!enabled) {
            return;
        }

        StopCurrentTask();

        int advanceDelay;
        int advanceTime;
        if (!CheckIfNight()) {
            advanceDelay = dayAdvanceDelay;
            advanceTime = dayAdvanceAmount;
            nightValues = false;
        }
        else {
            advanceDelay = nightAdvanceDelay;
            advanceTime = nightAdvanceAmount;
            nightValues = true;
        }

        if (advanceTime == 1 && advanceDelay == 1) {
            StartDefaultTime();
            return;
        }

        Bukkit.broadcastMessage(ChatColor.GRAY +"[DEBUG] Custom time on at "+ advanceTime +"/"+ advanceDelay);
        currentTask = Bukkit.getScheduler().runTaskTimer(CustomDayLength.getPlugin(CustomDayLength.class), this::AdvanceTime, 0, advanceDelay);
    }

    // Stops the current task script
    private void StopCurrentTask() {
        if (currentTask == null) {
            return;
        }
        currentTask.cancel();
        currentTask = null;
        Bukkit.broadcastMessage(ChatColor.GRAY +"[DEBUG] Custom time off");
    }

    // Starts default time
    private void StartDefaultTime() {
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        currentTask = Bukkit.getScheduler().runTaskTimer(CustomDayLength.getPlugin(CustomDayLength.class), this::CheckDefaultTime, 0, 20);
    }

    // Stops default time
    private void StopDefaultTime() {
        StopCurrentTask();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        StartCustomTime();
    }

    // Check when default time has ended
    private void CheckDefaultTime() {
        if (CheckForTimeChange()) {
            StopDefaultTime();
        }
    }

    // Advances the time artificially from normal gameplay
    private void AdvanceTime() {
        int advanceAmount;
        if (nightValues) {
            advanceAmount = nightAdvanceAmount;
        } else {
            advanceAmount = dayAdvanceAmount;
        }

        long currentTime = overworld.getTime();
        currentTime += advanceAmount;

        if (currentTime >= 24000) {
            currentTime -= 24000;
        }

        overworld.setTime(currentTime);

        if (CheckForTimeChange()) {
            StartCustomTime();
        }
    }

    // Gets new tick values for a given day speed multiplier
    private void UpdateDayValues() {
        double speedMultiplier = (double) defaultDayLength / (double) customDayLength;

        int[] tickValues = DecimalToFraction((long) speedMultiplier);
        dayAdvanceAmount = tickValues[0];
        dayAdvanceDelay = tickValues[1];
    }

    // Gets new tick values for a given night speed multiplier
    private void UpdateNightValues() {
        double speedMultiplier = (double) defaultNightLength / (double) customNightLength;

        int[] tickValues = DecimalToFraction((long) speedMultiplier);
        nightAdvanceAmount = tickValues[0];
        nightAdvanceDelay = tickValues[1];
    }

    // Get the fractional parts of a decimal
    private int[] DecimalToFraction(long decimal) {
        decimal = (long) (Math.floor(decimal * 100) / 100);
        String decimalText = Double.toString(Math.abs(decimal));
        int integerPlaces = decimalText.indexOf('.');
        int decimalPlaces = decimalText.length() - integerPlaces - 1;
        long powerTen = (long) Math.pow(10, decimalPlaces);

        BigInteger numerator = BigInteger.valueOf(decimal * powerTen);
        int gcd = numerator.gcd(BigInteger.valueOf(powerTen)).intValue();

        return new int[]{numerator.intValue() / gcd, (int) (powerTen / gcd)};
    }

    // Returns if it is currently night
    private boolean CheckIfNight() {
        long currentTime = overworld.getTime();
        return (currentTime > 13000 && currentTime <= 23000);
    }

    // Returns whether the time has changed between day / night
    private boolean CheckForTimeChange() {
        return (CheckIfNight() != nightValues);
    }


    // Returns the inputted minutes as a string of minutes + seconds
    private String GetMinutesSeconds(double minutes) {
        String decimalText = Double.toString(Math.abs(minutes));
        String[] splitText = decimalText.split("\\.");

        String returnText;
        if (splitText[1].equals("0")) {
            returnText = splitText[0] +" minutes";
        }
        else {
            double seconds = Double.parseDouble("0."+ splitText[1]);
            int secondsText = (int) (Math.round(seconds * 60));
            returnText = splitText[0] +" minutes, "+ secondsText +" seconds";
        }

        return returnText;
    }
}
