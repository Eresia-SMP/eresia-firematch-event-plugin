package fr.heav.eresia.eresiafirematchevent.commands;

import fr.heav.eresia.eresiafirematchevent.EresiaFireMatchEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StartGameCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public StartGameCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

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
        if (!sender.hasPermission("firematch.startGame")) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to start the game");
            return true;
        }
        switch (plugin.gameManager.startGame()) {
            case AlreadyStarted:
                sender.sendMessage(ChatColor.RED + "The game is already started");
                break;
            case Started:
                sender.sendMessage(ChatColor.WHITE + "The game has started");
                break;
        }

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
