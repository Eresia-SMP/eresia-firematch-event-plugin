package fr.heav.eresia.eresiafirematchevent;

import fr.heav.eresia.eresiafirematchevent.commands.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class EresiaFireMatchEvent extends JavaPlugin {
    public GameManager gameManager;
    public File saveFile;
    public YamlConfiguration save;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this, "mirage");

        MainFireMatchCommand mainFireMatchCommand = new MainFireMatchCommand();
        mainFireMatchCommand.addSubcommand("addspawn", new AddSpawnCommand(this));
        mainFireMatchCommand.addSubcommand("end", new EndCommand(this));
        mainFireMatchCommand.addSubcommand("join", new JoinGameCommand(this));
        mainFireMatchCommand.addSubcommand("leave", new LeaveGameCommand(this));
        mainFireMatchCommand.addSubcommand("lobby", new LobbyCommand(this));
        mainFireMatchCommand.addSubcommand("start", new StartGameCommand(this));
        mainFireMatchCommand.addSubcommand("stop", new StopCommand(this));
        Objects.requireNonNull(getCommand("firematch")).setExecutor(mainFireMatchCommand);
        getServer().getPluginManager().registerEvents(gameManager, this);

        saveFile = new File(getDataFolder(), "save.yml");
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
            saveResource("save.yml", false);
        }

        save = new YamlConfiguration();
        try {
            save.load(saveFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveSave() {
        try {
            save.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        saveSave();
    }
}
