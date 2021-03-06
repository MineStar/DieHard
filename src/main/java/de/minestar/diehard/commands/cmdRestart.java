package de.minestar.diehard.commands;

import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.Time;
import de.minestar.diehard.timers.TimerControl;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class cmdRestart extends AbstractExtendedCommand {
    public cmdRestart(String syntax, String arguments, String node) {
        super(DieHardCore.NAME, syntax, arguments, node);
    }

    @Override
    /**
     * Representing the command <br>
     * /restart <Time in minutes><br>
     * Restart the server
     * 
     * @param player
     *            Called the command
     * @param split
     */
    public void execute(String[] args, Player player) {
        if (args.length == 0) {
            // No parameters: show next restart time
            ChatUtils.writeInfo(player, pluginName, "N�chster Restart ist angesetzt f�r " + TimerControl.showNextRestartTime());
        } else if (UtilPermissions.playerCanUseCommand(player, "diehard.commands.restart")) {
            restart(player, args);
        } else {
            // Insufficient permissions
            ChatUtils.writeError(player, pluginName, "You are not allowed to use this command.");
        }
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        restart(console, args);
    }

    private boolean restart(CommandSender sender, String[] args) {
        int minutesUntilRestart;
        String message;
        // command has an argument
        if (args.length == 1) {
            // argument seems to have format HH:mm
            if (args[0].contains(":")) {
                Time restartTime = new Time(args[0]);
                if (restartTime.compareTo(new Time()) >= 0) {
                    DieHardCore.restartCheckThreadWithTimeAsHHmm(restartTime);
                    message = String.format("Server Neustart um %s", args[0]);
                    ChatUtils.writeInfo(sender, pluginName, message);
                    return true;
                } else {
                    // argument was not in format HH:mm
                    String errorMessage;
                    if (!restartTime.isValid()) {
                        errorMessage = "Argument has invalid time format";
                    } else {
                        errorMessage = "Unknown time conversion exception";
                    }
                    ChatUtils.writeError(sender, pluginName, errorMessage);
                    return false;
                }
            } else {
                // argument is something different than HH:mm
                try {
                    // try to get number from argument
                    minutesUntilRestart = Integer.valueOf(args[0]);
                    DieHardCore.restartCheckThreadWithTimeInMinutes(minutesUntilRestart);
                    message = String.format("Server Neustart in %d %s", minutesUntilRestart, minutesUntilRestart == 1 ? "Minute" : "Minuten");
                    ChatUtils.writeInfo(sender, pluginName, message);
                    return true;
                } catch (Exception e) {
                    // argument is not a number
                    if (args[0].equals("thetime")) {
                        // print current server time
                        Time now = new Time(new Date());
                        message = String.format("Current server time: %s", now.toString());
                        ChatUtils.writeInfo(sender, pluginName, message);
                    } else if (args[0].equals("activetimers")) {
                        // print active timer count
                        message = TimerControl.getActiveTimerInfo();
                        ChatUtils.writeInfo(sender, pluginName, message);
                    } else {
                        // unknown command
                        ChatUtils.writeError(sender, pluginName, "Unknown argument or argument is not a number");
                    }
                    return false;
                }
            }
        } else if (args.length == 0) {
            // no argument passed
            ChatUtils.writeInfo(sender, pluginName, "N�chster Restart ist angesetzt f�r " + TimerControl.showNextRestartTime());
            return true;
        } else {
            // more than one argument passed
            ChatUtils.writeError(sender, pluginName, "Ung�ltige Anzahl an Argumenten");
            return false;
        }
    }
}
