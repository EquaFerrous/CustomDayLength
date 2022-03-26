package me.equaferrous.customdaylength.timemanager;

import me.equaferrous.customdaylength.CustomDayLength;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class TimeManager {

    /*private int defaultDayLength = 12000;
    private int defaultNightLength = 12000;

    private int customDayLength = defaultDayLength;
    private int customNightLength = defaultNightLength;

    private int minCustomLength = 100;*/

    private static TimeManager instance;

    private long currentTime;
    private int dayAdvanceAmount = 1;
    private int timeAdvanceDelay = 1;

    private World overworld;
    private BukkitTask advanceTimeTask;

    // ----------------------------------------------

    public TimeManager() {
        instance = this;

        overworld = Bukkit.getWorlds().get(0);
    }

    // ------------------------------------------------

    public static TimeManager GetInstance() {
        return instance;
    }

    // -------------------------------------------------

    public void StartCustomRunning() {
        currentTime = overworld.getTime();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        advanceTimeTask = Bukkit.getScheduler().runTaskTimer(CustomDayLength.getPlugin(CustomDayLength.class), this::AdvanceTime, 0, timeAdvanceDelay);
    }

    public void StopCustomRunning() {
        advanceTimeTask.cancel();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
    }

    // ------------------------------------------------------

    private void AdvanceTime() {
        currentTime += dayAdvanceAmount;

        if (currentTime >= 24000) {
            currentTime -= 24000;
        }

        overworld.setTime(currentTime);
    }



    // -----------------------------------------------


    /*private void SetDayTickLength(int tickLength) {
        customDayLength = Math.max(minCustomLength, tickLength);
    }

    private void SetDayLengthMultiplier(float multiplier) {
        customDayLength = (int) (defaultDayLength * multiplier);
    }

    private void SetNightTickLength(int tickLength) {
        customNightLength = Math.max(minCustomLength, tickLength);
    }

    private void SetNightLengthMultiplier(float multiplier) {
        customNightLength = (int) (defaultNightLength * multiplier);
    }*/
}
