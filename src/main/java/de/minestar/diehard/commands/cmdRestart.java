package de.minestar.diehard.commands;

import org.bukkit.command.CommandSender;
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
        restart(player, args);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        restart(console, args);
    }

    private boolean restart(CommandSender sender, String[] args) {
        int minutesUntilRestart;
        try {
            minutesUntilRestart = Integer.valueOf(args[0]);
            DieHardCore.restartCheckThread(minutesUntilRestart);
            String message = String.format("Server Neustart in %d %s", minutesUntilRestart, minutesUntilRestart == 1 ? "Minute" : "Minuten");
            ChatUtils.writeInfo(sender, DieHardCore.NAME, message);
            return true;
        } catch (Exception e) {
            ChatUtils.writeError(sender, "Argument must be an Integer");
            return false;
        }
    }
}
