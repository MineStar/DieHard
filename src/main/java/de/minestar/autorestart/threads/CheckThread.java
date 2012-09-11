package de.minestar.autorestart.threads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CheckThread implements Runnable {
    private Calendar nextRestartTime;
    private List<Calendar> warningTimes;

    public CheckThread(List<Calendar> restartTimes, List<Calendar> warningTimes) {
        this.nextRestartTime = getNextRestartTime(restartTimes);
        System.out.println("Initialisiere CheckThread mit " + printCalendarTime(nextRestartTime));
        this.warningTimes = warningTimes;
    }

    public static String printCalendarTime(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
        return sdf.format(new Date(cal.getTimeInMillis()));
    }

    private Calendar getNextRestartTime(List<Calendar> restartTimes) {
        // read current time but remove everything but hours and minutes for
        // compare
        Calendar now = new GregorianCalendar();
        now.set(0, 0, 0);

        Calendar possibleRestartTime = null;
        // search times after current time and choose lowest
        for (Calendar cal : restartTimes) {
            if (possibleRestartTime == null) {
                if (cal.after(now)) {
                    possibleRestartTime = cal;
                }
            } else {
                if (cal.after(now)) {
                    if (possibleRestartTime.after(cal)) {
                        possibleRestartTime = cal;
                    }
                }
            }
        }
        // if possibleRestartTime is still null the next restart is after
        // midnight
        // that means we choose lowest time
        if (possibleRestartTime == null) {
            for (Calendar cal : restartTimes) {
                if (possibleRestartTime == null) {
                    possibleRestartTime = cal;
                } else {
                    if (cal.before(possibleRestartTime)) {
                        possibleRestartTime = cal;
                    }
                }
            }
        }
        // next restart time found now add date info again
        now = new GregorianCalendar();
        possibleRestartTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
        possibleRestartTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
        possibleRestartTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        possibleRestartTime.set(Calendar.SECOND, now.get(Calendar.SECOND));
        System.out.println("Nächste Restart Zeit: " + printCalendarTime(possibleRestartTime));
        // if possibleRestartTime is now before 'now' add a day to the object
        // to be sure use milliseconds to take care of month or year change
        if (possibleRestartTime.before(now)) {
            long millis = possibleRestartTime.getTimeInMillis();
            millis += TimeUnit.DAYS.toMillis(1);
            possibleRestartTime.setTimeInMillis(millis);
        }
        possibleRestartTime.set(Calendar.SECOND, 0);
        System.out.println("Verwendete Restart Zeit: " + printCalendarTime(possibleRestartTime));
        return possibleRestartTime;
    }

    @Override
    public void run() {
        Calendar now = new GregorianCalendar();
        Plugin p = Bukkit.getPluginManager().getPlugin("AutoRestart");

        System.out.println("Check Time");
        System.out.println("now = " + printCalendarTime(now));
        System.out.println("shutdown = " + printCalendarTime(nextRestartTime));
        long diff = nextRestartTime.getTimeInMillis() - now.getTimeInMillis();
        diff /= 1000 * 60;
        System.out.println("diff in minutes: " + diff);
        int minutesLeft;
        if (!warningTimes.isEmpty()) {
            System.out.println("naechste Warnzeit = " + printCalendarTime(warningTimes.get(0)));
            minutesLeft = warningTimes.get(0).get(Calendar.MINUTE);
        } else {
            minutesLeft = 0;
        }
        if (diff > 0) {
            if (diff == minutesLeft) {
                System.out.println("noch " + minutesLeft + " Minuten");
                MessageThread msg = new MessageThread(minutesLeft);
                BukkitScheduler sched = Bukkit.getScheduler();
                sched.scheduleSyncDelayedTask(p, msg, 1);
                warningTimes.remove(0);
            }
        } else {
            System.out.println("restart now");
            StopThread stp = new StopThread();
            BukkitScheduler sched = Bukkit.getScheduler();
            sched.scheduleSyncRepeatingTask(p,  stp,  1, 20 * 10);
        }
    }
}
