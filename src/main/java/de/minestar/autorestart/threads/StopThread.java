package de.minestar.autorestart.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import de.minestar.autorestart.AutoRestartCore;

public class StopThread implements Runnable {

	@Override
	public void run() {
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + AutoRestartCore.NAME + "] Der Server wird jetzt neu gestartet!");
		Bukkit.savePlayers();
		for (World world : Bukkit.getWorlds()){
			world.save();
		}
		Bukkit.shutdown();		
	}
}
