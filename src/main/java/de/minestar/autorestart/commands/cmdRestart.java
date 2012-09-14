package de.minestar.autorestart.commands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.autorestart.core.AutoRestartCore;
import de.minestar.autorestart.threads.CheckThread;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;

public class cmdRestart extends AbstractExtendedCommand {
    public cmdRestart(String syntax, String arguments, String node) {
        super(AutoRestartCore.NAME, syntax, arguments, node);
        System.out.println("inst CMd");
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
        restart(args);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        restart(args);
    }

    private void restart(String[] args) {
        CheckThread.setNextRestart(args[0]);
    }
}
