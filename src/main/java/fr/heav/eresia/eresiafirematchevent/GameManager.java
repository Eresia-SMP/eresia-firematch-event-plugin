package fr.heav.eresia.eresiafirematchevent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager {
    private class Participant {
        public Player player;
        public Location originalLocation;
        public Team playerTeam;

        Participant(Player player, Location originalLocation, Team playerTeam) {
            this.player = player;
            this.originalLocation = originalLocation;
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
        Participant participant = new Participant(player, player.getLocation().clone(), participantTeam);

        Location lobbyLocation = getLocationFromString(player.getServer(), Objects.requireNonNull(EresiaFireMatchEvent.save.getString("lobby")));
        player.teleport(lobbyLocation);

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
        return ParticipantLeaveResult.Left;
    }
    public enum ParticipantLeaveResult {
        Left,
        PlayerNotInGame,
    }
}
