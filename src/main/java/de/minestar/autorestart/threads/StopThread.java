package de.minestar.autorestart.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import de.minestar.autorestart.core.AutoRestartCore;

public class StopThread implements Runnable {
    private static boolean messageShown = false;

    @Override
    public void run() {
        if (!messageShown) {
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + AutoRestartCore.NAME + "] Der Server wird jetzt neu gestartet!");
            messageShown = true;
        } else {
            Bukkit.savePlayers();
            for (World world : Bukkit.getWorlds()) {
                world.save();
            }
            Bukkit.shutdown();
        }
    }
}
