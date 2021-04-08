package fr.heav.eresia.eresiafirematchevent;

import fr.heav.eresia.eresiafirematchevent.commands.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EresiaFireMatchEvent extends JavaPlugin {
    private Map<String, GameManager> gameManagers = new HashMap<>();
    public YamlConfiguration save;
    public File saveFile;

    @Override
    public void onEnable() {
        MainFireMatchCommand mainFireMatchCommand = new MainFireMatchCommand();
        mainFireMatchCommand.addSubcommand("addspawn", new AddSpawnCommand(this));
        mainFireMatchCommand.addSubcommand("end", new EndCommand(this));
        mainFireMatchCommand.addSubcommand("join", new JoinGameCommand(this));
        mainFireMatchCommand.addSubcommand("leave", new LeaveGameCommand(this));
        mainFireMatchCommand.addSubcommand("setlobby", new SetLobbyCommand(this));
        mainFireMatchCommand.addSubcommand("start", new StartGameCommand(this));
        mainFireMatchCommand.addSubcommand("stop", new StopCommand(this));
        mainFireMatchCommand.addSubcommand("loadsave", new LoadSaveCommand(this));
        mainFireMatchCommand.addSubcommand("savesave", new SaveSaveCommand(this));
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

    public @Nullable GameManager loadGame(String name) {
        if (!save.getStringList("gameNames").contains(name))
            return null;
        if (gameManagers.containsKey(name))
            return gameManagers.get(name);
        GameManager gameManager = new GameManager(this, name);
        getServer().getPluginManager().registerEvents(gameManager, this);
        gameManagers.put(name, gameManager);
        return gameManager;
    }
    public List<String> getGameNames() {
        return save.getStringList("gameNames");
    }

    public void saveSave() {
        try {
            save.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadSave() {
        try {
            save.load(saveFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        saveSave();
    }
}
