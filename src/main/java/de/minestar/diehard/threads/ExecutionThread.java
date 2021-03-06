package de.minestar.diehard.threads;

import org.bukkit.Bukkit;

import de.minestar.diehard.enums.TimerType;

public class ExecutionThread implements Runnable {
    TimerType timerType;
    private String message;

    public ExecutionThread(TimerType type, String message) {
        this.timerType = type;
        this.message = message;
    }

    @Override
    public void run() {
        switch (timerType) {
            case WarningTimer :
                Bukkit.broadcastMessage(this.message);
                break;
            case RestartTimer :
                // safely stop the server
                Bukkit.shutdown();
                break;
        }
    }

}
