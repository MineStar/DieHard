package de.minestar.diehard.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.diehard.core.DateTimeHelper;
import de.minestar.diehard.core.DieHardCore;
import de.minestar.diehard.core.Time;
import de.minestar.diehard.threads.CheckThread;
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
        restart(player, args);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        restart(console, args);
    }

    private boolean restart(CommandSender sender, String[] args) {
        int minutesUntilRestart;
        if (args.length == 1) {
            if (args[0].contains(":")) {
                Time restartTime = DateTimeHelper.getOnlyTimeLong(args[0]);
                if (restartTime.compareTo(new Time(0, 0)) >= 0) {
                    DieHardCore.restartCheckThreadWithTimeAsHHmm(restartTime);
                    String message = String.format("Server Neustart um %s", args[0]);
                    ChatUtils.writeInfo(sender, DieHardCore.NAME, message);
                    return true;
                } else {
                    String errorMessage;
                    if (!restartTime.isValid()) {
                      //  errorMessage = "Argument must has not format HH:mm";
                    //} else if (restartTime == -2) {
                        errorMessage = "Argument has invalid time format";
                    } else {
                        // should never reach here, but be prepared for future
                        // return value extensions
                        errorMessage = "Unknown time conversion exception";
                    }
                    ChatUtils.writeError(sender, DieHardCore.NAME, errorMessage);
                    return false;
                }
            } else {
                try {
                    minutesUntilRestart = Integer.valueOf(args[0]);
                    DieHardCore.restartCheckThreadWithTimeInMinutes(minutesUntilRestart);
                    String message = String.format("Server Neustart in %d %s", minutesUntilRestart, minutesUntilRestart == 1 ? "Minute" : "Minuten");
                    ChatUtils.writeInfo(sender, DieHardCore.NAME, message);
                    return true;
                } catch (Exception e) {
                    ChatUtils.writeError(sender, DieHardCore.NAME, "Argument must be an Integer");
                    return false;
                }
            }
        } else if (args.length == 0) {
            ChatUtils.writeInfo(sender, DieHardCore.NAME, "Nächster Restart ist angesetzt für " + CheckThread.showNextRestartTime());
            return true;
        } else {
            ChatUtils.writeError(sender, DieHardCore.NAME, "Ungültige Anzahl an Argumenten");
            return false;
        }
    }
}
