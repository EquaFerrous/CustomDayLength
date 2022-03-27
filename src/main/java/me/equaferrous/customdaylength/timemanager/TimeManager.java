package me.equaferrous.customdaylength.timemanager;

import me.equaferrous.customdaylength.CustomDayLength;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigInteger;

public class TimeManager {

    /*
    private int defaultNightLength = 12000;

    private int customNightLength = defaultNightLength;

    private int minCustomLength = 100;*/

    private static TimeManager instance;



    private long currentTime;
    private int dayAdvanceAmount = 1;
    private int timeAdvanceDelay = 1;

    private int defaultDayLength = 24000;
    private int customDayLength;

    private World overworld;
    private BukkitTask advanceTimeTask;
    private boolean running;

    // ----------------------------------------------

    public TimeManager() {
        instance = this;

        overworld = Bukkit.getWorlds().get(0);
        customDayLength = defaultDayLength;
        running = false;
    }

    // ------------------------------------------------

    public static TimeManager GetInstance() {
        return instance;
    }

    // -------------------------------------------------

    // Starts the custom time script
    public void StartCustomRunning() {
        if (running) {
            StopCustomRunning();
        }

        currentTime = overworld.getTime();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        running = true;
        Bukkit.broadcastMessage(ChatColor.GRAY +"Custom time on at "+ dayAdvanceAmount +"/"+ timeAdvanceDelay);

        advanceTimeTask = Bukkit.getScheduler().runTaskTimer(CustomDayLength.getPlugin(CustomDayLength.class), this::AdvanceTime, 0, timeAdvanceDelay);
    }

    // Stops the custom time script, reverting to default time
    public void StopCustomRunning() {
        if (advanceTimeTask != null) {
            advanceTimeTask.cancel();
            advanceTimeTask = null;
        }
        running = false;
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Bukkit.broadcastMessage(ChatColor.GRAY +"Custom time off");
    }

    // Sets a new day length as a multiplier of the default length
    public void SetDayLengthMultiplier(double lengthMultiplier) {
        if (lengthMultiplier == 1.0) {
            StopCustomRunning();
            return;
        }

        double newLength = defaultDayLength * lengthMultiplier;
        double speedMultiplier = defaultDayLength / newLength;
        int[] newTickValues = GetCustomTickValues(speedMultiplier);
        dayAdvanceAmount = newTickValues[0];
        timeAdvanceDelay = newTickValues[1];

        StartCustomRunning();
    }

    // ------------------------------------------------------

    private void AdvanceTime() {
        currentTime += dayAdvanceAmount;

        if (currentTime >= 24000) {
            currentTime -= 24000;
        }

        overworld.setTime(currentTime);
    }

    // Gets new tick values for a given multiplier - Returns [advanceAmount, advanceDelay]
    private int[] GetCustomTickValues(double speedMultiplier) {
        BigInteger numerator = BigInteger.valueOf((long) (speedMultiplier * 10));
        int gcd = numerator.gcd(BigInteger.TEN).intValue();

        return new int[]{numerator.intValue() / gcd, 10 / gcd};
    }

    // -----------------------------------------------


    /*
    public void SetDayTickLength(int tickLength) {
        customDayLength = Math.max(minCustomLength, tickLength);
    }

    private void SetNightTickLength(int tickLength) {
        customNightLength = Math.max(minCustomLength, tickLength);
    }

    private void SetNightLengthMultiplier(float multiplier) {
        customNightLength = (int) (defaultNightLength * multiplier);
    }*/
}
