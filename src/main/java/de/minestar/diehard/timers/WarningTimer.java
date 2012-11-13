package de.minestar.diehard.timers;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import de.minestar.diehard.core.DieHardCore;

public class WarningTimer extends TimerTask {
    private int warnTime;
    
    public WarningTimer(int warnTime) {
        this.warnTime = warnTime;
    }

    public void run() {
        String message = String.format("[%s] ACHTUNG !!! Der Server wird in %d Minute%s neu gestartet!", DieHardCore.NAME, this.warnTime, this.warnTime > 1 ? "n" : "");
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + message);
    }
}
