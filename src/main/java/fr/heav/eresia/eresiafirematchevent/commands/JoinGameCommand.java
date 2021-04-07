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

public class JoinGameCommand implements SubCommand {
    @Override
    public String getDescription() {
        return "Join une game";
    }
    @Override
    public String getHelp() {
        return "join [user that should join the game]";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player target;
        String targetName;
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify who should join the game");
            return true;
        }
        if (args.length >= 2) {
            target = sender.getServer().getPlayer(args[1]);
            targetName = target.getName();
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Could not find player "+args[1]);
                return true;
            }
        }
        else {
            target = (Player)sender;
            targetName = "You";
        }

        switch (EresiaFireMatchEvent.gameManager.addParticipant(target)) {
            case Joined:
                sender.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + targetName + ChatColor.RESET + ChatColor.WHITE + " has successfully joined to the game");
                break;
            case GameAlreadyStarted:
                sender.sendMessage(ChatColor.RED + targetName + " can't join the game if it's already started");
                break;
            case PlayerAlreadyInGame:
                sender.sendMessage(ChatColor.RED + targetName + " is already in the game");
                break;
        }

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 2) {
            return null;
        }

        return new ArrayList<>();
    }
}
