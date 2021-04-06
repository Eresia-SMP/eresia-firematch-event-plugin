package fr.heav.eresia.eresiafirematchevent;

import fr.heav.eresia.eresiafirematchevent.commands.JoinGameCommand;
import fr.heav.eresia.eresiafirematchevent.commands.MainFireMatchCommand;
import fr.heav.eresia.eresiafirematchevent.commands.StartGameCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EresiaFireMatchEvent extends JavaPlugin {

    @Override
    public void onEnable() {
        MainFireMatchCommand mainFireMatchCommand = new MainFireMatchCommand();
        mainFireMatchCommand.addSubcommand("join", new JoinGameCommand());
        mainFireMatchCommand.addSubcommand("start", new StartGameCommand());
        Objects.requireNonNull(getCommand("firematch")).setExecutor(mainFireMatchCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
