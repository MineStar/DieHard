package de.minestar.diehard.timers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.Settings;
import de.minestar.diehard.core.Time;
import de.minestar.diehard.enums.TimerType;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class TimerControl {
    private static List<ExecutionTimerAndTaskInfo> timerInfo = new ArrayList<ExecutionTimerAndTaskInfo>();
    private static String nextRestartTime;
    private static int minutesUntilExecution;

    public void cancelTimers() {
        for (ExecutionTimerAndTaskInfo info : TimerControl.timerInfo) {
            info.getTimer().cancel();
        }
        TimerControl.timerInfo.clear();
    }

    public TimerControl(int minutes) {
        // init Timers for restart in given minutes
        TimerControl.minutesUntilExecution = minutes;
        Time now = new Time(new Date());
        TimerControl.nextRestartTime = now.add(new Time(TimerControl.minutesUntilExecution)).toString();
        ConsoleUtils.printInfo(DieHardCore.NAME, "Restart Zeit geaendert auf: " + TimerControl.nextRestartTime);
        startTimers();
    }

    public TimerControl(List<Time> restartTimes, List<Time> warningTimes) {
        // init Timers from given settings value
        Time nextRestartTime = getNextRestartTime(restartTimes);
        Time now = new Time(new Date());
        TimerControl.minutesUntilExecution = now.difference(nextRestartTime).toMinutes();
        TimerControl.nextRestartTime = now.add(new Time(TimerControl.minutesUntilExecution)).toString();

        this.startTimers();
    }

    private void startTimers() {
        // start restart Timer
        ExecutionTimerAndTaskInfo execTimerAndTaskInfo;

        execTimerAndTaskInfo = new ExecutionTimerAndTaskInfo(TimerType.RestartTimer, "Restart", TimerControl.minutesUntilExecution);
        TimerControl.timerInfo.add(execTimerAndTaskInfo);

        execTimerAndTaskInfo = new ExecutionTimerAndTaskInfo(TimerType.WarningTimer, "LastWarning", TimerControl.minutesUntilExecution, "Yippie-Ya-Yeah Schweinebacke!");
        TimerControl.timerInfo.add(execTimerAndTaskInfo);

        // start all warning timers
        for (Time warnTime : Settings.getWarningTimes()) {
            int warnTimeMinutes = warnTime.toMinutes();
            if ((warnTimeMinutes > 0) && (warnTimeMinutes <= TimerControl.minutesUntilExecution)) {
                // Warn Time Verzögerung in Abhängigkeit von restart Zeit
                // festlegen
                int difference = TimerControl.minutesUntilExecution - warnTimeMinutes;
                String message = String.format("ACHTUNG !!! Der Server wird in %d Minute%s neu gestartet!", warnTimeMinutes, warnTimeMinutes > 1 ? "n" : "");
                String timerName = String.format("%d minutes left", warnTimeMinutes);
                execTimerAndTaskInfo = new ExecutionTimerAndTaskInfo(TimerType.WarningTimer, timerName, difference, message);
                TimerControl.timerInfo.add(execTimerAndTaskInfo);
            }
        }
    }

    public static String showNextRestartTime() {
        return TimerControl.nextRestartTime;
    }

    public static String getActiveTimerInfo() {
        String result = "Information about active timers\n";
        for (ExecutionTimerAndTaskInfo info : TimerControl.timerInfo) {
            result += String.format("%s\n", info.getTask().toString());
        }
        return result;
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
