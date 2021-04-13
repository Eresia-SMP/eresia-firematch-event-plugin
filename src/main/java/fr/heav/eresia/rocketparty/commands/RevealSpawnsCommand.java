package fr.heav.eresia.rocketparty.commands;

import fr.heav.eresia.rocketparty.RocketParty;
import fr.heav.eresia.rocketparty.GameManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RevealSpawnsCommand implements SubCommand {
    private RocketParty plugin;
    public RevealSpawnsCommand(RocketParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "Reveal all spawn point of a map for 30s";
    }
    @Override
    public String getHelp() {
        return "revealspawns <game name>";
    }
    @Override
    public String getPermission() {
        return "firematch.revealSpawns";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the game name");
            return true;
        }
        GameManager gameManager = plugin.loadGame(args[1]);
        if (gameManager == null) {
            sender.sendMessage(ChatColor.RED + "This game doesn't exist");
            return true;
        }

        List<ArmorStand> armorStands = gameManager.getSettings().getRespawnLocations().stream()
                .map((Location spawn) -> {
                    World world = spawn.getWorld();
                    ArmorStand armorStand = (ArmorStand)world.spawnEntity(spawn, EntityType.ARMOR_STAND);
                    armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1));
                    armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.PLAYER_HEAD));
                    armorStand.addScoreboardTag("spawnvis");
                    armorStand.setInvulnerable(true);
                    return armorStand;
                })
                .collect(Collectors.toList());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (ArmorStand armorStand : armorStands) {
                armorStand.remove();
            }
        }, 30 * 20);

        sender.sendMessage(ChatColor.WHITE + "All spawns are now revealed for 30s");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("firematch.revealSpawns"))
            return new ArrayList<>();
        if (args.length == 2)
            return plugin.getGameNames();
        return new ArrayList<>();
    }
}
