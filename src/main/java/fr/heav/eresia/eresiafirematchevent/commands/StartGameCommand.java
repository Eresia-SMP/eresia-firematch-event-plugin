package fr.heav.eresia.eresiafirematchevent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StartGameCommand implements SubCommand {
    @Override
    public String getDescription() {
        return "Start une game";
    }
    @Override
    public String getHelp() {
        return "start";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }
}
