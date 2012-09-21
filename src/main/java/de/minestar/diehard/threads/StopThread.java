package de.minestar.diehard.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import de.minestar.diehard.core.DieHardCore;

public class StopThread implements Runnable {
    private static boolean messageShown = false;

    @Override
    public void run() {
        // Thread will run twice
        // first time to broadcast a final warning
        // second time to safely stop the server
        if (!messageShown) {
            String message = String.format("[%s] ACHTUNG !!! Der Server wird JETZT neu gestartet!", DieHardCore.NAME);
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
