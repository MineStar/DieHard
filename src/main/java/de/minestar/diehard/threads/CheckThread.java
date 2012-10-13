package de.minestar.diehard.threads;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.Settings;
import de.minestar.diehard.core.Time;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class CheckThread implements Runnable {
    private static Time nextRestartTime;
    private List<Time> warningTimes;
    private int warningTimesIndex;

    private CheckThread() {
        this.warningTimes = Settings.getWarningTimes();
        this.warningTimesIndex = 0;
    }

    public CheckThread(List<Time> restartTimes) {
        this();
        CheckThread.nextRestartTime = getNextRestartTime(restartTimes);
    }

    public CheckThread(int minutesUntilRestart) {
        this();
        CheckThread.setNextRestart(minutesUntilRestart);
    }

    public static String showNextRestartTime() {
        return CheckThread.nextRestartTime.toString();
    }

    private Time getNextRestartTime(List<Time> restartTimes) {
        // read current time but remove everything
        // but hours and minutes for compare
        Time now = new Time(new Date());

        Time possibleRestartTime = restartTimes.get(0);
        // search restart times after current time or stick to the first in list
        for (Time date : restartTimes) {
            if (date.compareTo(now) > 0) {
                possibleRestartTime = date;
                break;
            }
        }

        ConsoleUtils.printInfo(DieHardCore.NAME, "Naechste Restart Zeit: " + possibleRestartTime.toString());
        return possibleRestartTime;
    }

    private Time getNextWarningTime(List<Time> warnTimes, Time timeLeft) {
        Time nextWarnTime;

        // find best fitting warning until restart from sorted list
        if (warningTimesIndex < warnTimes.size()) {
            nextWarnTime = warnTimes.get(warningTimesIndex);
            if (nextWarnTime.compareTo(timeLeft) > 0) {
                warningTimesIndex++;
                return getNextWarningTime(warnTimes, timeLeft);
            }
        } else {
            nextWarnTime = new Time(0, 0);
        }
        return nextWarnTime;
    }

    private static void setNextRestart(int minutesUntilRestart) {
        Time now = new Time(new Date());
        CheckThread.nextRestartTime = now.add(new Time(minutesUntilRestart));

        ConsoleUtils.printInfo(DieHardCore.NAME, "Restart Zeit geaendert auf: " + CheckThread.nextRestartTime.toString());
    }

    @Override
    public void run() {
        int lastWarning;
        Time nextWarnTime;
        // current time as milliseconds since epoch for compare
        Time now = new Time(new Date());

        Time diff = now.difference(nextRestartTime);
        nextWarnTime = getNextWarningTime(warningTimes, diff);
        System.out.println("diff = " + diff.toString());
        if (diff.isGreater(new Time(0,0))) {
            if (diff.equals(nextWarnTime)) {
                // remaining time until restart equals next warning time
                // --> broadcast message to players
                MessageThread msg = new MessageThread(nextWarnTime.toMinutes());
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
            //sched.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin(DieHardCore.NAME), stp, 0, DieHardCore.secondsToTicks(lastWarning));
            System.out.println("Jetzt waere Restart");
        }
    }
}
