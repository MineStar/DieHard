package de.minestar.autorestart.core;

import java.io.File;

import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.autorestart.threads.CheckThread;
import de.minestar.minestarlibrary.AbstractCore;

public class AutoRestartCore extends AbstractCore {
    public static final String NAME = "AutoRestart";

    private CheckThread checkThread;

    @Override
    protected boolean createThreads() {
        this.checkThread = new CheckThread(Settings.getRestartTimes(), Settings.getWarningTimes());
        return true;
    }

    @Override
    protected boolean startThreads(BukkitScheduler scheduler) {
        // start CheckThread with 1 minute delay and repeat every minute
        scheduler.scheduleAsyncRepeatingTask(this, this.checkThread, secondsToTicks(60), secondsToTicks(60));
        return super.startThreads(scheduler);
    }

    @Override
    protected boolean loadingConfigs(File dataFolder) {
        return Settings.init(dataFolder, NAME, getDescription().getVersion());
    }

    public static int secondsToTicks(int seconds) {
        int ticksPerSecond = 20;
        return ticksPerSecond * seconds;
    }
}
