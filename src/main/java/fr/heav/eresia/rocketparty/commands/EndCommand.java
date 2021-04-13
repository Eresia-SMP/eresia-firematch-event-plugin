package fr.heav.eresia.rocketparty.commands;

import fr.heav.eresia.rocketparty.RocketParty;
import fr.heav.eresia.rocketparty.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EndCommand implements SubCommand {
    private RocketParty plugin;
    public EndCommand(RocketParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Transition the game into the end scene";
    }
    @Override
    public String getHelp() {
        return "end <game name>";
    }
    @Override
    public String getPermission() {
        return "firematch.endGame";
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

        switch (gameManager.endGame()) {
            case Ended:
                sender.sendMessage(ChatColor.WHITE + "The game has been ended");
                break;
            case AlreadyEnded:
                sender.sendMessage(ChatColor.WHITE + "This game cannot be ended");
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
