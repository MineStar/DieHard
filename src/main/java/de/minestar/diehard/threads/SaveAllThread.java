package de.minestar.diehard.threads;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import de.minestar.diehard.core.DieHardCore;

public class SaveAllThread implements Runnable {

    @Override
    public void run() {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[" + DieHardCore.NAME + "] " + ChatColor.DARK_GREEN + "Saving playerdata and worlds...");
        Bukkit.getServer().savePlayers();
        List<World> worldList = Bukkit.getWorlds();
        for (World world : worldList) {
            world.save();
        }
        Bukkit.broadcastMessage(ChatColor.AQUA + "[" + DieHardCore.NAME + "] " + ChatColor.DARK_GREEN + "Done!");
    }

}
