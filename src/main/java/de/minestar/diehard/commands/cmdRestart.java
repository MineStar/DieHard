package de.minestar.diehard.commands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.diehard.core.DieHardCore;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class cmdRestart extends AbstractCommand {
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
        if (!restart(args)) {
            ChatUtils.writeError(player, "Argument muss Ganzzahl sein");
        }
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        if (!restart(args)) {
            ChatUtils.writeError(console, "Argument was not an Integer");
        }
    }

    private boolean restart(String[] args) {
        int minutesUntilRestart;
        try {
            minutesUntilRestart = Integer.valueOf(args[0]);
            DieHardCore.restartCheckThread(minutesUntilRestart);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
