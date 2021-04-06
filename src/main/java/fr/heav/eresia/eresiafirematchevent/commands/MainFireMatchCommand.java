package fr.heav.eresia.eresiafirematchevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MainFireMatchCommand implements CommandExecutor, TabCompleter {
    private final HashMap<String, SubCommand> subCommands = new HashMap<>();

    public void addSubcommand(@NotNull String name, @NotNull SubCommand subCommand) {
        subCommands.put(name, subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED+"Invalid usage, valid subcommands:\n"+getHelp(label));
            return true;
        }
        String subCommandName = args[0].toLowerCase();
        if (subCommandName.equals("help")) {
            if (args.length >= 2 && subCommands.containsKey(args[1].toLowerCase())) {
                SubCommand subCommand = subCommands.get(args[1].toLowerCase());
                sender.sendMessage(ChatColor.GREEN+"Usage:\n"+ChatColor.WHITE+"/"+label+" "+subCommand.getHelp());
            }
            else {
                sender.sendMessage(ChatColor.GREEN+"Usage:\n"+ChatColor.WHITE+getHelp(label));
            }
            return true;
        }

        SubCommand subCommand = subCommands.get(subCommandName);
        if (subCommand == null) {
            return true;
        }

        subCommand.onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subCommandsNames = new ArrayList<>(subCommands.keySet());
            subCommandsNames.add("help");
            Collections.sort(subCommandsNames);
            return subCommandsNames;
        }
        if (args.length == 2 && args[0].equals("help")) {
            return new ArrayList<>(subCommands.keySet());
        }
        if (args.length >= 2 && subCommands.containsKey(args[0].toLowerCase())) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            return subCommand.onTabComplete(sender, command, alias, args);
        }

        return new ArrayList<>();
    }

    public String getHelp(@NotNull String label) {
        StringBuilder message = new StringBuilder();/*
        if (invalidUsage) {
            message.append(ChatColor.RED).append("Invalid usage, valid subcommands:\n");
        }*/
        message.append(ChatColor.WHITE).append("/").append(label).append(" help").append(ChatColor.GRAY).append(" - Sends this message").append(ChatColor.WHITE);
        for (Map.Entry<String, SubCommand> subCommand : subCommands.entrySet()) {
            message.append("\n/").append(label).append(" ").append(subCommand.getKey())
                    .append(ChatColor.GRAY).append(" - ").append(subCommand.getValue().getDescription())
                    .append(ChatColor.WHITE);
        }
        return message.toString();
    }
}
