package de.minestar.diehard.core;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.diehard.commands.cmdRestart;
import de.minestar.diehard.threads.CheckThread;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;

public class DieHardCore extends AbstractCore {
    public static final String NAME = "DieHard";

    private static CheckThread checkThread;

    public DieHardCore() {
        super(NAME);
    }

    @Override
    protected boolean createThreads() {
        checkThread = new CheckThread(Settings.getRestartTimes());
        return true;
    }

    @Override
    protected boolean startThreads(BukkitScheduler scheduler) {
        // start CheckThread with 5 seconds delay and repeat every minute
        scheduler.scheduleAsyncRepeatingTask(this, checkThread, secondsToTicks(5), secondsToTicks(60));
        return true;
    }

    @Override
    protected boolean loadingConfigs(File dataFolder) {
        return Settings.init(dataFolder, NAME, getDescription().getVersion());
    }

    @Override
    protected boolean createCommands() {
        //@formatter:off
        cmdList = new CommandList(NAME,
                // RESTART COMMAND
                new cmdRestart("/restart", "[Time in minutes]", "diehard.commands.restart")
            );
            //@formatter:on
        return true;
    }

    public static int secondsToTicks(int seconds) {
        int ticksPerSecond = 20;
        return ticksPerSecond * seconds;
    }

    public static void restartCheckThread(int minutesUntilRestart) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(DieHardCore.NAME);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.cancelTasks(plugin);
        checkThread = new CheckThread(minutesUntilRestart);
        scheduler.scheduleAsyncRepeatingTask(plugin, checkThread, secondsToTicks(5), secondsToTicks(60));
    }
}
