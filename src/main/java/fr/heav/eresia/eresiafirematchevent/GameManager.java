package fr.heav.eresia.eresiafirematchevent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager implements Listener {
    private class Participant {
        public Player player;
        public Location originalLocation;
        public GameMode originalGameMode;
        public Team playerTeam;

        Participant(Player player, Team playerTeam) {
            this.player = player;
            this.originalLocation = player.getLocation().clone();
            this.originalGameMode = player.getGameMode();
            this.playerTeam = playerTeam;
        }
    }

    private final Map<Player, Participant> participants = new HashMap<>();
    private final Random random = new Random();
    private boolean isStarted = false;
    private Scoreboard scoreboard;
    private Scoreboard getScoreboard() {
        if (scoreboard == null)
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        return scoreboard;
    }

    public Location getLocationFromString(Server server, String locationStr) {
        String[] splittedPos = locationStr.split(";");

        return new Location(
                server.getWorld(splittedPos[0]),
                Float.parseFloat(splittedPos[1]),
                Float.parseFloat(splittedPos[2]),
                Float.parseFloat(splittedPos[3]),
                Float.parseFloat(splittedPos[4]),
                Float.parseFloat(splittedPos[5]));
    }
    public String getStringFromLocation(Location location) {
        return location.getWorld().getName()+";"+location.getX()+";"+location.getY()+";"+location.getZ()+";"+location.getYaw()+";"+location.getPitch();
    }

    public @Nullable Location getRandomLocation() {
        List<String> locations = EresiaFireMatchEvent.save.getStringList("spawns");
        if (locations.size() == 0)
            return null;

        return getLocationFromString(
                Bukkit.getServer(),
                locations.get(random.nextInt(locations.size()))
        );
    }

    public void setLobby(Location location) {
        EresiaFireMatchEvent.save.set("lobby", getStringFromLocation(location));
    }

    public @NotNull ParticipantJoinResult addParticipant(@NotNull Player player) {
        if (isStarted)
            return ParticipantJoinResult.GameAlreadyStarted;
        if (participants.containsKey(player))
            return ParticipantJoinResult.PlayerAlreadyInGame;

        Team participantTeam = getScoreboard().registerNewTeam(player.getName().substring(0,Math.min(player.getName().length()-1, 15)));
        participantTeam.setAllowFriendlyFire(false);
        participantTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        Participant participant = new Participant(player, participantTeam);

        player.setGameMode(GameMode.ADVENTURE);

        String stringLobbyLocation = EresiaFireMatchEvent.save.getString("lobby");
        if (stringLobbyLocation != null) {
            Location lobbyLocation = getLocationFromString(player.getServer(), stringLobbyLocation);
            player.teleport(lobbyLocation);
        }

        participants.put(player, participant);
        return ParticipantJoinResult.Joined;
    }
    public enum ParticipantJoinResult {
        Joined,
        PlayerAlreadyInGame,
        GameAlreadyStarted,
    }

    public ParticipantLeaveResult removeParticipant(@NotNull Player player) {
        if (!participants.containsKey(player))
            return ParticipantLeaveResult.PlayerNotInGame;
        Participant participant = participants.remove(player);
        participant.playerTeam.unregister();
        player.teleport(participant.originalLocation);
        player.setGameMode(participant.originalGameMode);
        return ParticipantLeaveResult.Left;
    }
    public enum ParticipantLeaveResult {
        Left,
        PlayerNotInGame,
    }

    @EventHandler
    public void onPlayerChangeGamemode(PlayerGameModeChangeEvent event) {
        if (participants.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public void addSpawnpoint(Location location) {
        List<String> spawns = EresiaFireMatchEvent.save.getStringList("spawns");
        spawns.add(getStringFromLocation(location));
        EresiaFireMatchEvent.save.set("spawns", spawns);
    }
}
