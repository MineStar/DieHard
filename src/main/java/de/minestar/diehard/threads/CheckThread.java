package de.minestar.diehard.threads;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.DateTimeHelper;
import de.minestar.diehard.core.Settings;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class CheckThread implements Runnable {
    private static long nextRestartTime;
    private List<Long> warningTimes;
    private int warningTimesIndex;

    private CheckThread() {
        this.warningTimes = Settings.getWarningTimes();
        this.warningTimesIndex = 0;
    }

    public CheckThread(List<Long> restartTimes) {
        this();
        CheckThread.nextRestartTime = getNextRestartTime(restartTimes);
    }

    public CheckThread(int minutesUntilRestart) {
        this();
        CheckThread.setNextRestart(minutesUntilRestart);
    }

    public static String showNextRestartTime() {
        return DateTimeHelper.convertMillisToTime(CheckThread.nextRestartTime);
    }

    private long getNextRestartTime(List<Long> restartTimes) {
        // read current time but remove everything
        // but hours and minutes for compare
        long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());

        long possibleRestartTime = restartTimes.get(0);
        // search restart times after current time or stick to the first in list
        for (long date : restartTimes) {
            if (date > nowOnlyTime) {
                possibleRestartTime = date;
                break;
            }
        }

        ChatUtils.writeInfo(Bukkit.getConsoleSender(), DieHardCore.NAME, "Naechste Restart Zeit: " + DateTimeHelper.convertMillisToTime(possibleRestartTime));
        return possibleRestartTime;
    }

    private long getNextWarningTime(List<Long> warnTimes, long minutesLeft) {
        long nextWarnTime;

        // find best fitting warning until restart from sorted list
        if (warningTimesIndex < warnTimes.size()) {
            nextWarnTime = TimeUnit.MILLISECONDS.toMinutes(warnTimes.get(warningTimesIndex));
            if (nextWarnTime > minutesLeft) {
                warningTimesIndex++;
                return getNextWarningTime(warnTimes, minutesLeft);
            }
        } else {
            nextWarnTime = 0;
        }
        return nextWarnTime;
    }

    private static void setNextRestart(int minutesUntilRestart) {
        long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());
        CheckThread.nextRestartTime = nowOnlyTime + TimeUnit.MINUTES.toMillis(minutesUntilRestart);
        // remove extra days from nextRestartTime
        long surplusDays = CheckThread.nextRestartTime / TimeUnit.DAYS.toMillis(1);
        if (surplusDays > 0) {
            CheckThread.nextRestartTime -= TimeUnit.DAYS.toMillis(surplusDays);
        }

        ChatUtils.writeInfo(Bukkit.getConsoleSender(), DieHardCore.NAME, "Restart Zeit geaendert auf: " + DateTimeHelper.convertMillisToTime(CheckThread.nextRestartTime));
    }

    @Override
    public void run() {
        int lastWarning;
        long nextWarnTime;
        // current time as milliseconds since epoch for compare
        long nowOnlyTime = DateTimeHelper.getOnlyTime(new Date());

        long diff = TimeUnit.MILLISECONDS.toMinutes(DateTimeHelper.getTimeDifference(nowOnlyTime, nextRestartTime));
        nextWarnTime = getNextWarningTime(warningTimes, diff);

        if (diff > 0) {
            if (diff == nextWarnTime) {
                // remaining time until restart equals next warning time
                // --> broadcast message to players
                MessageThread msg = new MessageThread(nextWarnTime);
                BukkitScheduler sched = Bukkit.getScheduler();
                sched.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin(DieHardCore.NAME), msg, 1);
            }
            // TODO Fix possible error of skipping restart time
            // it's possible that restart is skipped due to high server
            // load shortly before restrat should take place
            // e.g. server restart is set to 10:00 and Checkthread runs at 59
            // seconds of minute. High server load could lead to CheckThread run
            // being delayed to next second. That means current time is 10:01
            // and diff is the time until 10:00 of the next day.
            // This should only be a rare case.
        } else {
            // initiate server restart
            lastWarning = Settings.getLastWarning();
            StopThread stp = new StopThread();
            BukkitScheduler sched = Bukkit.getScheduler();
            sched.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin(DieHardCore.NAME), stp, 1, DieHardCore.secondsToTicks(lastWarning));
        }
    }
}
