package de.minestar.diehard.timers;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.Time;
import de.minestar.diehard.enums.TimerType;
import de.minestar.diehard.threads.ExecutionThread;

public class ExecutionTimerTask extends TimerTask {
    private int minutesUntilExecution;
    private String message;
    private TimerType timerType;
    private String name;

    public ExecutionTimerTask(TimerType type, String name, int minutes) {
        this.timerType = type;
        this.minutesUntilExecution = minutes;
        this.name = String.format("%s %s", type.toString(), name);
    }

    public ExecutionTimerTask(TimerType type, String name, int minutes, String message) {
        this(type, name, minutes);
        this.message = String.format(ChatColor.AQUA + "[%s]" + ChatColor.RED + " %s", DieHardCore.NAME, message);
    }

    @Override
    public void run() {
        if (this.minutesUntilExecution-- == 0) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(DieHardCore.NAME);
            if (this.timerType == TimerType.RestartTimer) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ExecutionThread(timerType, message));
            } else {
                Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new ExecutionThread(timerType, message));
            }
            this.cancel();
        }
    }

    @Override
    public String toString() {
        return String.format("%s: Time until execution = %s", this.name, new Time(this.minutesUntilExecution + 1));
    }

    public TimerType getType() {
        return timerType;
    }
}
