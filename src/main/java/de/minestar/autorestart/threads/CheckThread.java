package de.minestar.autorestart.threads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CheckThread implements Runnable{
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
		// read current time but remove everything but hours and minutes for compare
		Calendar now = new GregorianCalendar();
		now.set(0, 0, 0);
		
		Calendar possibleRestartTime = null;
		// search times after current time and choose lowest
		for (Calendar cal : restartTimes) {
			if (possibleRestartTime == null) {
				if (cal.after(now)) {
					possibleRestartTime = cal;
				}
			}
			else {
				if (cal.after(now)) {
					if (possibleRestartTime.after(cal)){
						possibleRestartTime = cal;
					}
				}				
			}
		}
		// if possibleRestartTime is still null the next restart is after midnight
		// that means we choose lowest time
		if (possibleRestartTime == null) {
			for (Calendar cal : restartTimes) {
				if (possibleRestartTime == null) {
					possibleRestartTime = cal;
				}
				else {
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
			millis += 1000 * 60 * 60 * 24;
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
		System.out.println("naechste Warnzeit = " + printCalendarTime(warningTimes.get(0)));
		long diff = nextRestartTime.getTimeInMillis() - now.getTimeInMillis();
		System.out.println("diff in ms: " + diff);
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
