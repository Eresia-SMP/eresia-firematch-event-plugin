package fr.heav.eresia.eresiafirematchevent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.FireworkMeta;
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
    private boolean isEnded = false;
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective killsObjs;
    private final ItemStack firework;
    private final ItemStack crossBow;

    public boolean getIsStarted() {
        return isStarted;
    }
    public boolean getIsEnded() {
        return isEnded;
    }

    GameManager() {
        recreateKillsObjective();

        firework = new ItemStack(Material.FIREWORK_ROCKET, 10);
        FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();
        for (int i = 0; i < 10; i++) {
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .flicker(false)
                    .trail(false)
                    .withColor(Color.WHITE)
                    .withFade(Color.WHITE)
                    .with(FireworkEffect.Type.BALL)
                    .build());
        }
        fireworkMeta.setPower(2);
        firework.setItemMeta(fireworkMeta);

        crossBow = new ItemStack(Material.CROSSBOW, 1);
        CrossbowMeta crossbowMeta = (CrossbowMeta) crossBow.getItemMeta();
        crossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
        crossbowMeta.setUnbreakable(true);
        crossbowMeta.addChargedProjectile(firework.clone());
        crossBow.setItemMeta(crossbowMeta);
    }
    private void recreateKillsObjective() {
        if (killsObjs != null)
            killsObjs.unregister();
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

    public @NotNull ParticipantJoinResult addParticipant(@NotNull Player player, boolean shouldJoinIfGameIsStarted) {
        if (isEnded)
            return ParticipantJoinResult.GameInEndScene;
        if (isStarted && !shouldJoinIfGameIsStarted)
            return ParticipantJoinResult.GameStarted;
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
        player.getInventory().clear();

        if (isStarted)
            makeAPlayerStartGame(player);
        else {
            Location spawnLocation = getLobby();
            if (spawnLocation != null)
                player.teleport(spawnLocation);
        }

        for (Player player2 : participants.keySet()) {
            player2.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GREEN + " has joined the game");
        }

        participants.put(player, participant);
        return ParticipantJoinResult.Joined;
    }
    public enum ParticipantJoinResult {
        Joined,
        PlayerAlreadyInGame,
        GameInEndScene,
        GameStarted,
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
            if (player.getKiller() == null) {
                event.deathMessage(
                        Component.text()
                            .append(Component.text(player.getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                            .append(Component.text(" has been killed by... nobody ?").color(NamedTextColor.YELLOW))
                            .build()
                );
            }
            else {
                event.deathMessage(
                        Component.text()
                                .append(Component.text(player.getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                                .append(Component.text(" has been killed by ").color(NamedTextColor.YELLOW))
                                .append(Component.text(player.getKiller().getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                                .build()
                );
            }
            Score score = killsObjs.getScore(player.getKiller().getName());
            score.setScore(score.getScore()+1);

        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!participants.containsKey(event.getPlayer()))
            return;
        Player player = event.getPlayer();
        if (!isStarted) {
            Location lobby = getLobby();
            if (lobby != null)
                event.setRespawnLocation(lobby);
        }
        else {
            Location spawnLocation = getRandomLocation();
            if (spawnLocation != null)
                event.setRespawnLocation(spawnLocation);
            player.getInventory().setItem(4, crossBow.clone());
        }
    }
    @EventHandler
    public void onPlayerLoseHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!participants.containsKey(event.getPlayer()))
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!participants.containsKey(event.getEntity()))
            return;
        Player player = (Player)event.getEntity();
        if (event.getBow().getType() == Material.CROSSBOW) {
            player.getInventory().setItemInOffHand(firework.clone());
        }
    }
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!participants.containsKey(event.getWhoClicked()))
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        if (!participants.containsKey(event.getPlayer()) && isStarted)
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerSwapHands(PlayerInteractAtEntityEvent event) {
        if (!participants.containsKey(event.getPlayer()))
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!participants.containsKey(event.getEntity()))
            return;
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (!isStarted) {
                event.setCancelled(true);
            }
            else {
                event.setDamage(10);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!participants.containsKey(event.getPlayer()) || event.getClickedBlock() == null)
            return;
        String blockName = event.getClickedBlock().getType().name();
        if (blockName.contains("TRAPDOOR") || blockName.contains("DOOR"))
            event.setCancelled(true);
    }

    public StartGameResult startGame() {
        if (isStarted)
            return StartGameResult.AlreadyStarted;

        for (Player player : participants.keySet()) {
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "THE GAME HAS STARTED!");
            player.sendTitle("The game has started!", "Good luck", 10, 70, 10);
            makeAPlayerStartGame(player);
        }
        isStarted = true;
        return StartGameResult.Started;
    }
    private void makeAPlayerStartGame(Player player) {
        player.teleport(Objects.requireNonNull(getRandomLocation()));

        PlayerInventory playerInventory = player.getInventory();

        playerInventory.clear();
        playerInventory.setItemInOffHand(firework.clone());
        playerInventory.setHeldItemSlot(4);
        playerInventory.setItemInMainHand(crossBow.clone());
    }
    public enum StartGameResult {
        Started,
        AlreadyStarted,
    }

    private BossBar endGameTimingBossBar = null;
    private int endGameTimerTaskId = 0;
    public EndGameResult endGame() {
        if (isEnded || !isStarted)
            return EndGameResult.AlreadyEnded;
        isEnded = true;

        endGameTimingBossBar = Bukkit.getServer().createBossBar("firematchendgameremaning", BarColor.WHITE, BarStyle.SOLID);
        endGameTimingBossBar.setProgress(0.0);
        endGameTimingBossBar.setTitle("Congratulations !");

        for (Player player : participants.keySet()) {
            endGameTimingBossBar.addPlayer(player);
            player.sendTitle(
                    ChatColor.GREEN + "End !",
                    ChatColor.UNDERLINE + "" + ChatColor.DARK_GRAY + "The game is finished",
                    10, 40, 10
            );
            player.setGameMode(GameMode.SPECTATOR);
        }

        long startTime = System.nanoTime();

        endGameTimerTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EresiaFireMatchEvent.globalInstance, () -> {
            endGameTimingBossBar.setProgress(((double)(System.nanoTime()-startTime)) / (30_000_000_000.0D));
        }, 0, 10);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EresiaFireMatchEvent.globalInstance, this::stopGame, 20 * 30);

        return EndGameResult.Ended;
    }
    public enum EndGameResult {
        Ended,
        AlreadyEnded,
    }

    public StopGameResult stopGame() {
        if (!isStarted)
            return StopGameResult.AlreadyStopped;

        recreateKillsObjective();

        if (isEnded || endGameTimingBossBar != null || endGameTimerTaskId != 0) {
            Bukkit.getServer().getScheduler().cancelTask(endGameTimerTaskId);
            endGameTimingBossBar.removeAll();
            endGameTimerTaskId = 0;
            endGameTimingBossBar = null;
        }

        Location lobby = getLobby();
        for (Player player : participants.keySet()) {
            if (lobby != null)
                player.teleport(lobby);
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
        }

        isStarted = false;
        isEnded = false;
        return StopGameResult.Stopped;
    }
    public enum StopGameResult {
        Stopped,
        AlreadyStopped,
    }

    public void addSpawnpoint(Location location) {
        List<String> spawns = EresiaFireMatchEvent.save.getStringList("spawns");
        spawns.add(getStringFromLocation(location));
        EresiaFireMatchEvent.save.set("spawns", spawns);
    }
}
