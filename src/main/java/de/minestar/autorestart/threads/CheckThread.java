package de.minestar.autorestart.threads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.autorestart.core.AutoRestartCore;
import de.minestar.autorestart.core.DateTimeHelper;

public class CheckThread implements Runnable {
    private Long nextRestartTime;
    private List<Long> warningTimes;

    public CheckThread(List<Long> restartTimes, List<Long> warningTimes) {
        this.nextRestartTime = getNextRestartTime(restartTimes);
        this.warningTimes = warningTimes;
    }

    public static String printCalendarTime(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
        return sdf.format(new Date(cal.getTimeInMillis()));
    }

    private Long getNextRestartTime(List<Long> restartTimes) {
        // read current time but remove everything
        // but hours and minutes for compare
        Long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());

        long possibleRestartTime = 0;
        // search times after current time and choose lowest
        for (Long date : restartTimes) {
            if (possibleRestartTime == 0) {
                if (date > nowOnlyTime) {
                    System.out.println("moegliche Restart Zeit gefunden");
                    possibleRestartTime = date;
                }
            } else {
                if (date > nowOnlyTime) {
                    System.out.println("moegliche Restart Zeit gefunden");
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
        System.out.println("Naechste Restart Zeit: " + possibleRestartTime);
        return possibleRestartTime;
    }

    @Override
    public void run() {
        long nextWarnTime;
        Long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());

        long difference = DateTimeHelper.getTimeDifference(nowOnlyTime, nextRestartTime);
        long diff = TimeUnit.MILLISECONDS.toMinutes(difference);
        System.out.println("Minutes until restart: " + diff);
        if (!warningTimes.isEmpty()) {
            nextWarnTime = TimeUnit.MILLISECONDS.toMinutes(warningTimes.get(0));
            System.out.println("naechste Warnzeit:" + nextWarnTime + " Minuten vor Neustart");
        } else {
            nextWarnTime = 0;
        }
        
        if (diff > 0) {
            if (diff == nextWarnTime) {
                MessageThread msg = new MessageThread(nextWarnTime);
                BukkitScheduler sched = Bukkit.getScheduler();
                sched.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin(AutoRestartCore.NAME), msg, 1);
                warningTimes.remove(0);
            }
        } else {
            StopThread stp = new StopThread();
            BukkitScheduler sched = Bukkit.getScheduler();
            sched.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin(AutoRestartCore.NAME), stp, 1, AutoRestartCore.secondsToTicks(10));
        }
    }
}
