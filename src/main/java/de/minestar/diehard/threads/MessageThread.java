package de.minestar.diehard.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import de.minestar.diehard.core.DieHardCore;

public class MessageThread implements Runnable {
    private final long minutes;

    public MessageThread(long mins) {
        this.minutes = mins;
    }

    @Override
    public void run() {
        String message = String.format("[%s] ACHTUNG !!! Der Server wird in %d Minuten neu gestartet!", DieHardCore.NAME, this.minutes);
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + message);
    }
}
