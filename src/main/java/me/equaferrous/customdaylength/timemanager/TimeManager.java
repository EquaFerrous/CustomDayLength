package me.equaferrous.customdaylength.timemanager;

import me.equaferrous.customdaylength.CustomDayLength;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;


public class TimeManager {

    private static TimeManager instance;

    private double dayAdvanceAmount;
    private double nightAdvanceAmount;

    private final int defaultDayLength = 14000;
    private final int defaultNightLength = 10000;
    private int customDayLength;
    private int customNightLength;

    private final World overworld;
    private BukkitTask currentTask;
    private boolean enabled;
    private boolean nightValues;

    private double currentTime;

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
        double nightLength = ((double) customNightLength / 20) / 60;

        text += "[ Day length : "+ GetMinutesSeconds(dayLength) +" ]";
        text += "\n[ Night length : "+ GetMinutesSeconds(nightLength) +" ]";

        recipient.sendMessage(ChatColor.GRAY + text);

        if (!enabled) {
            recipient.sendMessage(ChatColor.RED + "Changes will not take effect until the plugin is enabled -->\n/EnableCustomTime");
        }
    }

    // Skips an amount of time caused by an external source
    public void SkipTime(long skipAmount) {
        long fullTime = overworld.getFullTime();
        fullTime += skipAmount;
        overworld.setFullTime(fullTime);

        currentTime = overworld.getTime();
        AdvanceTime();
    }

    public boolean GetIfEnabled() {
        return enabled;
    }

    // ------------------------------------------------------

    // Starts the custom time script
    private void StartCustomTime() {
        if (!enabled) {
            return;
        }

        StopCurrentTask();

        currentTime = overworld.getTime();

        double advanceTime;
        if (!CheckIfNight()) {
            advanceTime = dayAdvanceAmount;
            nightValues = false;
        }
        else {
            advanceTime = nightAdvanceAmount;
            nightValues = true;
        }

        if (advanceTime == 1) {
            StartDefaultTime();
            return;
        }

        currentTask = Bukkit.getScheduler().runTaskTimer(CustomDayLength.getPlugin(CustomDayLength.class), this::AdvanceTime, 0, 1);
    }

    // Stops the current task script
    private void StopCurrentTask() {
        if (currentTask == null) {
            return;
        }
        currentTask.cancel();
        currentTask = null;
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
    }

    // Check when default time has ended
    private void CheckDefaultTime() {
        currentTime = overworld.getTime();
        if (CheckForTimeChange()) {
            StopDefaultTime();
            StartCustomTime();
        }
    }

    // Advances the time artificially from normal gameplay
    private void AdvanceTime() {
        double advanceAmount;
        if (nightValues) {
            advanceAmount = nightAdvanceAmount;
        } else {
            advanceAmount = dayAdvanceAmount;
        }

        currentTime += advanceAmount;

        if (currentTime >= 24000) {
            currentTime %= 24000;
        }

        long roundedTime = Math.round(currentTime);
        overworld.setTime(roundedTime);

        if (CheckForTimeChange()) {
            StartCustomTime();
        }
    }

    // Gets new tick values for a given day speed multiplier
    private void UpdateDayValues() {
        dayAdvanceAmount = (double) defaultDayLength / (double) customDayLength;
    }

    // Gets new tick values for a given night speed multiplier
    private void UpdateNightValues() {
        nightAdvanceAmount = (double) defaultNightLength / (double) customNightLength;
    }

    // Returns if it is currently night
    private boolean CheckIfNight() {
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
