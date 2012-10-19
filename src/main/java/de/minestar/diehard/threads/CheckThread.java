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
    private static int minutesUntilRestart;
    private static String nextRestartTime;
    private List<Time> warningTimes;
    private int warningTimesIndex;

    private CheckThread() {
        this.warningTimes = Settings.getWarningTimes();
        this.warningTimesIndex = 0;
    }

    public CheckThread(List<Time> restartTimes) {
        this();
        Time nextRestartTime = getNextRestartTime(restartTimes);
        Time now = new Time(new Date());
        CheckThread.minutesUntilRestart = now.difference(nextRestartTime).toMinutes();
        CheckThread.nextRestartTime = now.add(new Time(CheckThread.minutesUntilRestart)).toString();
    }

    public CheckThread(int minutesUntilRestart) {
        this();
        CheckThread.setNextRestart(minutesUntilRestart);
    }

    public static String showNextRestartTime() {
        return CheckThread.nextRestartTime;
    }

    private Time getNextRestartTime(List<Time> restartTimes) {
        // read current time but remove everything
        // but hours and minutes for compare
        Time now = new Time(new Date());

        Time possibleRestartTime = restartTimes.get(0);
        // search restart times after current time or stick to the first in list
        for (Time date : restartTimes) {
            if (date.isAfter(now)) {
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
            if (nextWarnTime.isAfter(timeLeft)) {
                warningTimesIndex++;
                return getNextWarningTime(warnTimes, timeLeft);
            }
        } else {
            nextWarnTime = new Time();
        }
        return nextWarnTime;
    }

    private static void setNextRestart(int minutesUntilRestart) {
        Time now = new Time(new Date());
        CheckThread.minutesUntilRestart = minutesUntilRestart;
        CheckThread.nextRestartTime = now.add(new Time(CheckThread.minutesUntilRestart)).toString();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Restart Zeit geaendert auf: " + now.add(new Time(minutesUntilRestart)).toString());
    }

    @Override
    public void run() {
        int lastWarning;
        Time nextWarnTime;
        // current time as milliseconds since epoch for compare

        nextWarnTime = getNextWarningTime(warningTimes, new Time(CheckThread.minutesUntilRestart));
        if (CheckThread.minutesUntilRestart > 0) {
            if (CheckThread.minutesUntilRestart == nextWarnTime.toMinutes()) {
                // remaining time until restart equals next warning time
                // --> broadcast message to players
                MessageThread msg = new MessageThread(nextWarnTime.toMinutes());
                BukkitScheduler sched = Bukkit.getScheduler();
                sched.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin(DieHardCore.NAME), msg, 1);
            }
        } else {
            // initiate server restart
            lastWarning = Settings.getLastWarning();
            StopThread stp = new StopThread();
            BukkitScheduler sched = Bukkit.getScheduler();
            sched.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin(DieHardCore.NAME), stp, 0, DieHardCore.secondsToTicks(lastWarning));
        }
        Time now = new Time(new Date());
        CheckThread.nextRestartTime = now.add(new Time(CheckThread.minutesUntilRestart--)).toString();
    }
}
