package fr.heav.eresia.eresiafirematchevent;

import fr.heav.eresia.eresiafirematchevent.commands.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public final class EresiaFireMatchEvent extends JavaPlugin {
    public static EresiaFireMatchEvent globalInstance;
    public static GameManager gameManager = new GameManager();
    public static File saveFile;
    public static YamlConfiguration save;

    @Override
    public void onEnable() {
        globalInstance = this;

        MainFireMatchCommand mainFireMatchCommand = new MainFireMatchCommand();
        mainFireMatchCommand.addSubcommand("join", new JoinGameCommand());
        mainFireMatchCommand.addSubcommand("leave", new LeaveGameCommand());
        mainFireMatchCommand.addSubcommand("start", new StartGameCommand());
        mainFireMatchCommand.addSubcommand("lobby", new LobbyCommand());
        Objects.requireNonNull(getCommand("firematch")).setExecutor(mainFireMatchCommand);

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
