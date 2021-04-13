package fr.heav.eresia.rocketparty.commands;

import fr.heav.eresia.rocketparty.RocketParty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SaveSaveCommand implements SubCommand {
    private RocketParty plugin;
    public SaveSaveCommand(RocketParty plugin) {
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
        return "firematch.saveSave";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.saveSave();
        sender.sendMessage("The save file has been saved");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
