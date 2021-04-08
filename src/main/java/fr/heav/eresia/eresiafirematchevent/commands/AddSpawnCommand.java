package fr.heav.eresia.eresiafirematchevent.commands;

import fr.heav.eresia.eresiafirematchevent.EresiaFireMatchEvent;
import fr.heav.eresia.eresiafirematchevent.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddSpawnCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public AddSpawnCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Ajoute un spawner";
    }
    @Override
    public String getHelp() {
        return "addspawn <game name>";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("firematch.addSpawn")) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to add a spawn point");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the game name");
            return true;
        }
        GameManager gameManager = plugin.loadGame(args[1]);
        if (gameManager == null) {
            sender.sendMessage(ChatColor.RED + "This game doesn't exist");
            return true;
        }

        Player player = (Player)sender;
        gameManager.getSettings().addRespawnLocation(player.getLocation());
        sender.sendMessage(ChatColor.WHITE + "The spawnpoint has been added");

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
