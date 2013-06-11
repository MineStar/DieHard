package de.minestar.diehard.threads;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.syncchest.library.PlayerUtils;

public class SaveAllThread implements Runnable {

    @Override
    public void run() {
        Player[] playerList = Bukkit.getOnlinePlayers();
        for (Player player : playerList) {
            PlayerUtils.sendMessage(player, ChatColor.DARK_GREEN, DieHardCore.NAME, "Saving playerdata and worlds...");
        }

        Bukkit.getServer().savePlayers();
        List<World> worldList = Bukkit.getWorlds();
        for (World world : worldList) {
            world.save();
        }

        playerList = Bukkit.getOnlinePlayers();
        for (Player player : playerList) {
            PlayerUtils.sendMessage(player, ChatColor.DARK_GREEN, DieHardCore.NAME, "Done!");
        }
    }

}
