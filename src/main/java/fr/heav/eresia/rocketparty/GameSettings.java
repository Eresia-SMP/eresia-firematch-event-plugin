package fr.heav.eresia.rocketparty;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameSettings {
    private @NotNull RocketParty plugin;
    private @NotNull String name;

    GameSettings(@NotNull RocketParty plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }
    private static @Nullable Location getLocationFromString(@Nullable String locationStr) {
        if (locationStr == null)
            return null;
        String[] splittedPos = locationStr.split(";");

        return new Location(
                Bukkit.getServer().getWorld(splittedPos[0]),
                Float.parseFloat(splittedPos[1]),
                Float.parseFloat(splittedPos[2]),
                Float.parseFloat(splittedPos[3]),
                Float.parseFloat(splittedPos[4]),
                Float.parseFloat(splittedPos[5]));
    }
    private static @Nullable String getStringFromLocation(@Nullable Location location) {
        if (location == null)
            return null;
        return location.getWorld().getName()+";"+location.getX()+";"+location.getY()+";"+location.getZ()+";"+location.getYaw()+";"+location.getPitch();
    }

    public int getRespawnDuration() {
        return plugin.save.getInt("games."+name+".respawn_duration", 3_000);
    }
    public void setRespawnDuration(int respawnDuration) {
        plugin.save.set("games."+name+".respawn_duration", respawnDuration);
    }

    public int getEndGameDuration() {
        return plugin.save.getInt("games."+name+".endgame_duration", 30_000);
    }
    public void setEndGameDuration(int endGameDuration) {
        plugin.save.set("games."+name+".endgame_duration", endGameDuration);
    }

    public @Nullable Location getRandomRespawn(Random random) {
        List<String> spawnList = plugin.save.getStringList("games."+name+".respawn_locations");
        if (spawnList.size() == 0)
            return null;
        return getLocationFromString(spawnList.get(random.nextInt(spawnList.size())));
    }
    public @NotNull List<Location> getRespawnLocations() {
        return plugin.save.getStringList("games."+name+".respawn_locations")
                .stream()
                .map(GameSettings::getLocationFromString)
                .collect(Collectors.toList());
    }
    public void setRespawnLocations(@NotNull List<Location> respawnLocations) {
        plugin.save.set("games."+name+".respawn_locations", respawnLocations
                .stream()
                .map(GameSettings::getStringFromLocation)
                .collect(Collectors.toList()));
    }
    public void addRespawnLocation(@NotNull Location respawnLocation) {
        List<String> respawnLocations = plugin.save.getStringList("games."+name+".respawn_locations");
        respawnLocations.add(getStringFromLocation(respawnLocation));
        plugin.save.set("games."+name+".respawn_locations", respawnLocations);
    }

    public @Nullable Location getLobbyLocation() {
        return getLocationFromString(plugin.save.getString("games."+name+".lobby"));
    }
    public void setLobbyLocation(@NotNull Location location) {
        plugin.save.set("games."+name+".lobby", getStringFromLocation(location));
    }
}
