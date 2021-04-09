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

public class CreateGameCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public CreateGameCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Create a new game";
    }
    @Override
    public String getHelp() {
        return "creategame <game name>";
    }
    @Override
    public String getPermission() {
        return "firematch.creategame";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must give a name to the new game");
            return true;
        }
        if (plugin.getGameNames().contains(args[1])) {
            sender.sendMessage(ChatColor.RED + "This game name is already taken");
            return true;
        }
        if (args[1].length() >= 16 || args[1].length() <= 2) {
            sender.sendMessage(ChatColor.RED + "The game name must be between 2 and 16 characters");
            return true;
        }
        plugin.createGame(args[1]);
        sender.sendMessage(ChatColor.WHITE + "The game has been created");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
