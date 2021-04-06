package fr.heav.eresia.eresiafirematchevent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class JoinGameCommand implements SubCommand {
    @Override
    public String getDescription() {
        return "Join une game";
    }
    @Override
    public String getHelp() {
        return "join [user that should join the game]";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }
}
