package fr.heav.eresia.eresiafirematchevent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.*;
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
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private final Objective killsObjs;

    GameManager() {
        killsObjs = scoreboard.registerNewObjective("kills", "THISISPLAYERKILLS", Component.text("Kills"));
        killsObjs.setDisplaySlot(DisplaySlot.SIDEBAR);
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
    public @Nullable Location getLobby() {
        String stringLobbyLocation = EresiaFireMatchEvent.save.getString("lobby");
        if (stringLobbyLocation == null)
            return null;
        return getLocationFromString(Bukkit.getServer(), stringLobbyLocation);
    }

    public @NotNull ParticipantJoinResult addParticipant(@NotNull Player player) {
        if (isStarted)
            return ParticipantJoinResult.GameAlreadyStarted;
        if (participants.containsKey(player))
            return ParticipantJoinResult.PlayerAlreadyInGame;

        Team participantTeam = scoreboard.registerNewTeam(player.getName().substring(0,Math.min(player.getName().length()-1, 15)));
        participantTeam.setAllowFriendlyFire(false);
        participantTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        participantTeam.addEntry(player.getName());
        player.setScoreboard(scoreboard);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1.0);
        Participant participant = new Participant(player, participantTeam);
        killsObjs.getScore(player.getName()).setScore(0);

        player.setGameMode(GameMode.ADVENTURE);

        Location lobbyLocation = getLobby();
        if (lobbyLocation != null)
            player.teleport(lobbyLocation);

        for (Player player2 : participants.keySet()) {
            player2.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GREEN + " has joined the game");
        }

        player.getInventory().clear();

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
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20.0);

        for (Player player2 : participants.keySet()) {
            player2.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.RED + " has left the game");
        }

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
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (participants.containsKey(event.getPlayer())) {
            removeParticipant(event.getPlayer());
            event.quitMessage(null);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!participants.containsKey(event.getEntity()))
            return;
        Player player = event.getEntity();
        if (!isStarted) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "How did you die when the game hasn't even started yet ??");
            event.deathMessage(Component.text(player.getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                    .append(Component.text(" is dead in a mysterious way").color(NamedTextColor.YELLOW)));
        }
        else {
            event.deathMessage(Component.text(player.getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                            .append(Component.text(" has been killed by ").color(NamedTextColor.YELLOW))
                            .append(Component.text(player.getKiller().getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
            Score score = killsObjs.getScore(player.getKiller().getName());
            score.setScore(score.getScore()+1);
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!participants.containsKey(event.getPlayer()))
            return;
        if (!isStarted) {
            Location lobby = getLobby();
            if (lobby != null)
                event.setRespawnLocation(lobby);
        }
        else {
            Location spawnLocation = getRandomLocation();
            if (spawnLocation != null)
                event.setRespawnLocation(spawnLocation);
        }
    }
    @EventHandler
    public void onPlayerLoseHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        event.setCancelled(true);
    }

    public StartGameResult startGame() {
        if (isStarted)
            return StartGameResult.AlreadyStarted;
        for (Player player : participants.keySet()) {
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "THE GAME HAS STARTED!");
            player.sendTitle("The game has started!", "Good luck", 10, 70, 10);
            player.teleport(Objects.requireNonNull(getRandomLocation()));
        }
        isStarted = true;
        return StartGameResult.Started;
    }
    public enum StartGameResult {
        Started,
        AlreadyStarted,
    }

    public void addSpawnpoint(Location location) {
        List<String> spawns = EresiaFireMatchEvent.save.getStringList("spawns");
        spawns.add(getStringFromLocation(location));
        EresiaFireMatchEvent.save.set("spawns", spawns);
    }
}
