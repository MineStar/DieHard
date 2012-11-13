package de.minestar.diehard.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.minestar.diehard.commands.cmdRestart;
import de.minestar.diehard.timers.TimerControl;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;

public class DieHardCore extends AbstractCore {
    public static final String NAME = "DieHard";

    private static TimerControl timerControl;

    public DieHardCore() {
        super(NAME);
    }

    @Override
    protected boolean loadingConfigs(File dataFolder) {
        return Settings.init(dataFolder, NAME, getDescription().getVersion());
    }

    @Override
    protected boolean createCommands() {
        //@formatter:off
        cmdList = new CommandList(NAME,
                // RESTART COMMAND
                new cmdRestart("/restart", "", "")
            );
            //@formatter:on
        return true;
    }

    @Override
    protected boolean commonEnable() {
        // create TimerControl which creates all necessary timers
        timerControl = new TimerControl(Settings.getRestartTimes(), Settings.getWarningTimes());
        return true;
    }

    @Override
    protected boolean commonDisable() {
        // Cancel all active timer on plugin disable
        timerControl.cancelTimers();
        return true;
    }

    public static void restartCheckThreadWithTimeInMinutes(int minutesUntilRestart) {
        // reload timer with minutes until restart
        timerControl.cancelTimers();
        timerControl = new TimerControl(minutesUntilRestart);
    }

    public static void restartCheckThreadWithTimeAsHHmm(Time restartTime) {
        // restart timers with given restart time
        List<Time> restartTimes = new ArrayList<Time>();
        restartTimes.add(restartTime);
        timerControl.cancelTimers();
        timerControl = new TimerControl(restartTimes, Settings.getWarningTimes());
    }
}
