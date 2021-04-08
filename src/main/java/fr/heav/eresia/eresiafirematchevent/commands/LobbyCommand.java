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

public class LobbyCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public LobbyCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Set the lobby of the game";
    }

    @Override
    public String getHelp() {
        return "lobby";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("firematch.setLobby")) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to change the lobby");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can change the lobby location");
            return true;
        }
        Player player = (Player) sender;
        plugin.gameManager.getSettings().setLobbyLocation(player.getLocation());
        player.sendMessage(ChatColor.WHITE + "The lobby location has been changed");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
