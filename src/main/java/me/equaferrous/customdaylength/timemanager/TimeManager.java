package me.equaferrous.customdaylength.timemanager;

import me.equaferrous.customdaylength.CustomDayLength;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigInteger;

public class TimeManager {

    private static TimeManager instance;

    private int dayAdvanceAmount = 1;
    private int dayAdvanceDelay = 1;
    private int nightAdvanceAmount = 1;
    private int nightAdvanceDelay = 1;

    private final int defaultDayLength = 12000;
    private final int defaultNightLength = 12000;
    private int customDayLength = defaultDayLength;
    private int customNightLength = defaultNightLength;

    private final World overworld;
    private BukkitTask currentTask;
    private boolean enabled;
    private boolean nightValues;

    // ----------------------------------------------

    public TimeManager() {
        instance = this;

        overworld = Bukkit.getWorlds().get(0);
        enabled = false;
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
        customDayLength = (int) ((minutes / 2) * 60 * 20);
        customNightLength = (int) ((minutes / 2) * 60 * 20);
        UpdateDayValues();
        UpdateNightValues();

        StartCustomTime();
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
        return (overworld.getTime() > 12000);
    }

    // Returns whether the time has changed between day / night
    private boolean CheckForTimeChange() {
        return (CheckIfNight() != nightValues);
    }
}
