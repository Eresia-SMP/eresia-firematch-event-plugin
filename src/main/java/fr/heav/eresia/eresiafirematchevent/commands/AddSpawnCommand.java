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

public class AddSpawnCommand implements SubCommand {
    @Override
    public String getDescription() {
        return "Ajoute un spawner";
    }
    @Override
    public String getHelp() {
        return "addspawn";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can add spawn points");
            return true;
        }
        Player player = (Player)sender;
        EresiaFireMatchEvent.gameManager.addSpawnpoint(player.getLocation());
        sender.sendMessage(ChatColor.WHITE + "The spawnpoint has been added");

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
