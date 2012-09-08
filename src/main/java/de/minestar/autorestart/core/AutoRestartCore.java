package de.minestar.autorestart.core;

import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.autorestart.threads.CheckThread;
import de.minestar.minestarlibrary.AbstractCore;

public class AutoRestartCore extends AbstractCore {
	public static final String NAME = "AutoRestart";
	
	private CheckThread checkThread;

	@Override
	protected boolean createThreads() {
		this.checkThread = new CheckThread();
		return super.createThreads();
	}

	@Override
	protected boolean startThreads(BukkitScheduler scheduler) {
		scheduler.scheduleAsyncRepeatingTask(this, this.checkThread, 20 * 60, 20 * 60);
		return super.startThreads(scheduler);
	}
}
