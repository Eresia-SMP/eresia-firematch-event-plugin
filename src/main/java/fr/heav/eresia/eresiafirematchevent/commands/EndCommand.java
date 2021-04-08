package fr.heav.eresia.eresiafirematchevent.commands;

import fr.heav.eresia.eresiafirematchevent.EresiaFireMatchEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EndCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public EndCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Transition the game into the end scene";
    }
    @Override
    public String getHelp() {
        return "end";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("firematch.endGame")) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to end the game");
            return true;
        }

        switch (plugin.gameManager.endGame()) {
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
        return new ArrayList<>();
    }
}
