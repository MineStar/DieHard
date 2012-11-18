package de.minestar.diehard.timers;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import de.minestar.diehard.core.Settings;
import de.minestar.diehard.enums.TimerType;

public class ExecutionTimerAndTaskInfo {
    private Timer timer;
    private ExecutionTimerTask task;

    public ExecutionTimerAndTaskInfo(TimerType type, String name, int minutes) {
        this.timer = new Timer();
        this.task = new ExecutionTimerTask(type, name, minutes);
        this.scheduleTimer();
    }
    
    public ExecutionTimerAndTaskInfo(TimerType type, String name, int minutes, String message) {
        this.timer = new Timer();
        this.task = new ExecutionTimerTask(type,  name, minutes, message);
        this.scheduleTimer();
    }

    public Timer getTimer() {
        return this.timer;
    }

    public ExecutionTimerTask getTask() {
        return this.task;
    }
    
    private void scheduleTimer() {
        long startDelay;
        if (task.getType() == TimerType.WarningTimer) {
            startDelay = 0;
        } else {
            startDelay = TimeUnit.SECONDS.toMillis(Settings.getLastWarning());
        }
        this.timer.scheduleAtFixedRate(this.task, startDelay, TimeUnit.MINUTES.toMillis(1));
    }
}
