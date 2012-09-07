package de.minestar.autorestart.threads;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CheckThread implements Runnable{
	private Calendar nextShutdown;
	
	public CheckThread() {
		// TODO soll aus einer Config geholt werden
		nextShutdown = new GregorianCalendar(2012, 8, 8, 1, 5);
	}

	@Override
	public void run() {
		Calendar now = new GregorianCalendar();
		Plugin p = Bukkit.getPluginManager().getPlugin("AutoRestart");
		
		System.out.println("Check Time");
		System.out.println("now = " + now.get(Calendar.MINUTE));
		System.out.println("shutdown = " + nextShutdown.get(Calendar.MINUTE));
		long diff = nextShutdown.getTimeInMillis() - now.getTimeInMillis();
		diff /= 60000;
		if (diff == 3) {
			System.out.println("noch 3");
			MessageThread msg = new MessageThread(3);
			BukkitScheduler sched = Bukkit.getScheduler();
			sched.scheduleSyncDelayedTask(p, msg, 1);
		} else if (diff == 2) {
			System.out.println("noch 2");
			MessageThread msg = new MessageThread(2);
			BukkitScheduler sched = Bukkit.getScheduler();
			sched.scheduleSyncDelayedTask(p, msg, 1);
		} else if (diff == 1) {
			System.out.println("noch 1");
			MessageThread msg = new MessageThread(1);
			BukkitScheduler sched = Bukkit.getScheduler();
			sched.scheduleSyncDelayedTask(p, msg, 1);
		} else if (diff == 0) {
			System.out.println("boom");
			StopThread stp = new StopThread();
			BukkitScheduler sched = Bukkit.getScheduler();
			sched.scheduleSyncDelayedTask(p, stp, 1);
		}
	}
}
