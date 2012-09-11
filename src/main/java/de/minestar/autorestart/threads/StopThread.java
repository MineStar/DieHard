package de.minestar.autorestart.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import de.minestar.autorestart.core.AutoRestartCore;

public class StopThread implements Runnable {

    @Override
    public void run() {
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + AutoRestartCore.NAME + "] Der Server wird jetzt neu gestartet!");
        // 10 seconds delay to see the broadcast
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("Sleep wurde unterbrochen");
        }
        Bukkit.savePlayers();
        for (World world : Bukkit.getWorlds()) {
            world.save();
        }
        Bukkit.shutdown();
    }
}
