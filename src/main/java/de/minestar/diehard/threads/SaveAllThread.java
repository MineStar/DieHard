package de.minestar.diehard.threads;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class SaveAllThread implements Runnable {

    @Override
    public void run() {
        Bukkit.getServer().savePlayers();
        List<World> worldList = Bukkit.getWorlds();
        for (World world : worldList) {
            world.save();
        }
    }

}
