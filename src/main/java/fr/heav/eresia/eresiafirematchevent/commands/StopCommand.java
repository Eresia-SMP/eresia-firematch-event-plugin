package fr.heav.eresia.eresiafirematchevent.commands;

import fr.heav.eresia.eresiafirematchevent.EresiaFireMatchEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StopCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public StopCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Stop the game";
    }
    @Override
    public String getHelp() {
        return "stop";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("firematch.stopGame")) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to stop the game");
            return true;
        }

        switch (plugin.gameManager.stopGame()) {
            case Stopped:
                sender.sendMessage(ChatColor.WHITE + "The game has been stopped");
                break;
            case AlreadyStopped:
                sender.sendMessage(ChatColor.RED + "You cannot stop this game");
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
