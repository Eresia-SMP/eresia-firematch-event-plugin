package fr.heav.eresia.rocketparty.commands;

import fr.heav.eresia.rocketparty.RocketParty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LoadSaveCommand implements SubCommand {
    private RocketParty plugin;
    public LoadSaveCommand(RocketParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Load the save file without saving the in-memory version";
    }
    @Override
    public String getHelp() {
        return "loadsave";
    }
    @Override
    public String getPermission() {
        return "firematch.loadSave";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.loadSave();
        sender.sendMessage("The save file has been loaded");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
