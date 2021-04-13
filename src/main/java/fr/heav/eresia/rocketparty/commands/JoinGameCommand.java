package fr.heav.eresia.rocketparty.commands;

import fr.heav.eresia.rocketparty.RocketParty;
import fr.heav.eresia.rocketparty.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JoinGameCommand implements SubCommand {
    private RocketParty plugin;
    public JoinGameCommand(RocketParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Join a game";
    }
    @Override
    public String getHelp() {
        return "join <game name> [user that should join the game]";
    }
    @Override
    public String getPermission() {
        return "firematch.joinMatch";
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
        Player target;
        String targetName;
        if (!(sender instanceof Player) && args.length < 3) {
            sender.sendMessage(ChatColor.RED + "You must specify who should join the game");
            return true;
        }
        if (args.length >= 3) {
            if (!sender.hasPermission("firematch.joinMatch.someoneElse")) {
                sender.sendMessage(ChatColor.RED + "You do not have the permission to make someone join a game");
                return true;
            }
            target = sender.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Could not find player "+args[2]);
                return true;
            }
            targetName = target.getName();
        }
        else {
            target = (Player)sender;
            targetName = "You";
        }

        switch (gameManager.addParticipant(target, sender.hasPermission("firematch.joinMatch.during"))) {
            case Joined:
                sender.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + targetName + ChatColor.RESET + ChatColor.WHITE + " has successfully joined to the game");
                break;
            case GameInEndScene:
                sender.sendMessage(ChatColor.RED + targetName + " can't join the game because it's currently in the end scene");
                break;
            case GameStarted:
                sender.sendMessage(ChatColor.RED + targetName + " can't join this game, it's still in progress");
                break;
            case PlayerAlreadyInGame:
                sender.sendMessage(ChatColor.RED + targetName + " is/are already in the game");
                break;
        }

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 2) {
            return plugin.getGameNames();
        }
        if (args.length == 3 && sender.hasPermission("firematch.joinMatch.someoneElse")) {
            return null;
        }
        return new ArrayList<>();
    }
}
