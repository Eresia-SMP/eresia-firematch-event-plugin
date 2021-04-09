package fr.heav.eresia.eresiafirematchevent.commands;

import fr.heav.eresia.eresiafirematchevent.EresiaFireMatchEvent;
import fr.heav.eresia.eresiafirematchevent.GameManager;
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
        return "start <game name>";
    }
    @Override
    public String getPermission() {
        return "firematch.startGame";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the game name");
            return true;
        }
        GameManager gameManager = plugin.loadGame(args[1]);
        if (gameManager == null) {
            sender.sendMessage(ChatColor.RED + "This game doesn't exist");
            return true;
        }

        switch (gameManager.startGame()) {
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
        if (args.length == 2) {
            return plugin.getGameNames();
        }
        return new ArrayList<>();
    }
}
