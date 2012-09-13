package de.minestar.autorestart.threads;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.autorestart.core.AutoRestartCore;
import de.minestar.autorestart.core.DateTimeHelper;
import de.minestar.autorestart.core.Settings;

public class CheckThread implements Runnable {
    private long nextRestartTime;
    private List<Long> warningTimes;

    public CheckThread(List<Long> restartTimes, List<Long> warningTimes) {
        this.nextRestartTime = getNextRestartTime(restartTimes);
        this.warningTimes = warningTimes;
    }

    private long getNextRestartTime(List<Long> restartTimes) {
        // read current time but remove everything
        // but hours and minutes for compare
        long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());

        long possibleRestartTime = 0;
        // search times after current time and choose lowest
        for (long date : restartTimes) {
            if (possibleRestartTime == 0) {
                if (date > nowOnlyTime) {
                    possibleRestartTime = date;
                }
            } else {
                if (date > nowOnlyTime) {
                    if (possibleRestartTime > date) {
                        possibleRestartTime = date;
                    }
                }
            }
        }
        // if possibleRestartTime is still null the next restart
        // is after midnight that means we choose lowest time
        if (possibleRestartTime == 0) {
            possibleRestartTime = restartTimes.get(0);
        }
        AutoRestartCore.printInfo("Naechste Restart Zeit: " + DateTimeHelper.convertMillisToTime(possibleRestartTime));
        return possibleRestartTime;
    }

    private long getNextWarningTime(List<Long> warnTimes, long minutesLeft) {
        long nextWarnTime;

        if (!warnTimes.isEmpty()) {
            nextWarnTime = TimeUnit.MILLISECONDS.toMinutes(warnTimes.get(0));
            if (nextWarnTime > minutesLeft) {
                warningTimes.remove(0);
                return getNextWarningTime(warnTimes, minutesLeft);
            }
        } else {
            nextWarnTime = 0;
        }
        return nextWarnTime;
    }

    @Override
    public void run() {
        int lastWarning;
        long nextWarnTime;
        long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());

        long difference = DateTimeHelper.getTimeDifference(nowOnlyTime, nextRestartTime);
        long diff = TimeUnit.MILLISECONDS.toMinutes(difference);
        nextWarnTime = getNextWarningTime(warningTimes, diff);
        
        if (diff > 0) {
            if (diff == nextWarnTime) {
                MessageThread msg = new MessageThread(nextWarnTime);
                BukkitScheduler sched = Bukkit.getScheduler();
                sched.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin(AutoRestartCore.NAME), msg, 1);
                warningTimes.remove(0);
            }
        } else {
            lastWarning = Settings.getLastWarning();
            StopThread stp = new StopThread();
            BukkitScheduler sched = Bukkit.getScheduler();
            sched.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin(AutoRestartCore.NAME), stp, 1, AutoRestartCore.secondsToTicks(lastWarning));
        }
    }
}
