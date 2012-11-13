package de.minestar.diehard.timers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.Settings;
import de.minestar.diehard.core.Time;
import de.minestar.diehard.timers.WarningTimer;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class TimerControl {
    private static List<Timer> timers = new ArrayList<Timer>();
    private static String nextRestartTime;
    private static int minutesUntilRestart;

    public void cancelTimers() {
        for (Timer timer : TimerControl.timers) {
            timer.cancel();
        }
        TimerControl.timers.clear();
    }

    public TimerControl(int minutes) {
        TimerControl.minutesUntilRestart = minutes;
        Time now = new Time(new Date());
        TimerControl.nextRestartTime = now.add(new Time(TimerControl.minutesUntilRestart)).toString();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Restart Zeit geaendert auf: " + TimerControl.nextRestartTime);
        startTimers();
    }

    public TimerControl(List<Time> restartTimes, List<Time> warningTimes) {
        Time nextRestartTime = getNextRestartTime(restartTimes);
        Time now = new Time(new Date());
        TimerControl.minutesUntilRestart = now.difference(nextRestartTime).toMinutes();
        TimerControl.nextRestartTime = now.add(new Time(TimerControl.minutesUntilRestart)).toString();

        this.startTimers();
    }

    private void startTimers() {
        Timer restartTimer = new Timer();
        restartTimer.schedule(new RestartTimer(), TimeUnit.MINUTES.toMillis(TimerControl.minutesUntilRestart), TimeUnit.SECONDS.toMillis(Settings.getLastWarning()));
        TimerControl.timers.add(restartTimer);

        for (Time warnTime : Settings.getWarningTimes()) {
            int warnTimeMinutes = warnTime.toMinutes();
            if ((warnTimeMinutes > 0) && (warnTimeMinutes <= TimerControl.minutesUntilRestart)) {
                Timer warningTimer = new Timer();
                // Warn Time Verzögerung in Abhängigkeit von restart Zeit
                // festlegen
                int difference = TimerControl.minutesUntilRestart - warnTimeMinutes;
                warningTimer.schedule(new WarningTimer(warnTimeMinutes), TimeUnit.MINUTES.toMillis(difference));
                TimerControl.timers.add(warningTimer);
            }
        }
    }

    public static String showNextRestartTime() {
        return TimerControl.nextRestartTime;
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
}
