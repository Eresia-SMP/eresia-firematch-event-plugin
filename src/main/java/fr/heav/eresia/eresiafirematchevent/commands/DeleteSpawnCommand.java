package fr.heav.eresia.eresiafirematchevent.commands;

import fr.heav.eresia.eresiafirematchevent.EresiaFireMatchEvent;
import fr.heav.eresia.eresiafirematchevent.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DeleteSpawnCommand implements SubCommand {
    private EresiaFireMatchEvent plugin;
    public DeleteSpawnCommand(EresiaFireMatchEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Delete all spawns around you";
    }
    @Override
    public String getHelp() {
        return "deletespawn <game name>";
    }
    @Override
    public String getPermission() {
        return "firematch.deletespawn";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }
        Player player = (Player)sender;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the game name");
            return true;
        }
        GameManager gameManager = plugin.loadGame(args[1]);
        if (gameManager == null) {
            sender.sendMessage(ChatColor.RED + "This game doesn't exist");
            return true;
        }

        int amountRemoved = 0;

        List<Location> spawns = gameManager.getSettings().getRespawnLocations();
        int i = 0;
        while (i < spawns.size()) {
            Location spawn = spawns.get(i);
            if (spawn.distanceSquared(player.getLocation()) <= 4.0) {
                spawns.remove(i);
                amountRemoved += 1;
            }
            else {
                i += 1;
            }
        }
        gameManager.getSettings().setRespawnLocations(spawns);

        if (amountRemoved == 0) {
            sender.sendMessage(ChatColor.RED + "No spawn were found");
        }
        else {
            sender.sendMessage(ChatColor.WHITE + "" + amountRemoved + " spawn point(s) were remove");
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
