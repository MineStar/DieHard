package de.minestar.autorestart.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import de.minestar.autorestart.core.AutoRestartCore;

public class MessageThread implements Runnable {
    private final int minutes;

    public MessageThread(int mins) {
        this.minutes = mins;
    }

    @Override
    public void run() {
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + AutoRestartCore.NAME + "] ACHTUNG !!! Der Server wird in " + this.minutes + " Minuten neu gestartet!");
    }
}
