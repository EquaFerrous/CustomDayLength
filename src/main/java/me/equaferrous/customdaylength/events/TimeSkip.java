package me.equaferrous.customdaylength.events;

import me.equaferrous.customdaylength.timemanager.TimeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class TimeSkip implements Listener {

    @EventHandler
    public void OnTimeSkip(TimeSkipEvent event) {
        if (!(event.getSkipReason() == TimeSkipEvent.SkipReason.CUSTOM)) {
            TimeManager timeManager = TimeManager.GetInstance();

            if (timeManager.GetIfEnabled()) {
                event.setCancelled(true);
                TimeManager.GetInstance().SkipTime(event.getSkipAmount());
            }
        }
    }
}
