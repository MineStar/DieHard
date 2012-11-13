package de.minestar.diehard.timers;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import de.minestar.diehard.core.DieHardCore;

public class RestartTimer extends TimerTask {
    private static boolean messageShown = false;
    
    public void run() {
     // Thread will run twice
        // first time to broadcast a final warning
        // second time to safely stop the server
        if (!messageShown) {
            String message = String.format("[%s] Yippie-Ya-Yeah Schweinebacke!", DieHardCore.NAME);
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + message);
            messageShown = true;
        } else {
            // be sure that everything gets saved before server shutdown
            Bukkit.savePlayers();
            for (World world : Bukkit.getWorlds()) {
                world.save();
            }
            Bukkit.shutdown();
        }
    }
}
