package fr.heav.eresia.rocketparty.gamemanager;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class Participant {
    public Player player;
    public Location originalLocation;
    public GameMode originalGameMode;
    public Team playerTeam;

    public Participant(Player player, Team playerTeam) {
        this.player = player;
        this.originalLocation = player.getLocation().clone();
        this.originalGameMode = player.getGameMode();
        this.playerTeam = playerTeam;
    }
}
