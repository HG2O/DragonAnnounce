package com.arkasurvie.dragon;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class DragonAnnounce extends JavaPlugin implements Listener {

    private final Random random = new Random();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("DragonAnnounce activé !");
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {

        if (!(event.getEntity() instanceof EnderDragon)) return;

        EnderDragon dragon = (EnderDragon) event.getEntity();

        Player killer = dragon.getKiller();

        if (killer == null) return;

        FileConfiguration config = getConfig();

        String title = config.getString("title");
        String subtitle = config.getString("subtitle");
        String chat = config.getString("chat-message");

        title = title.replace("%player%", killer.getName());
        subtitle = subtitle.replace("%player%", killer.getName());
        chat = chat.replace("%player%", killer.getName());

        ChatColor[] colors = {
                ChatColor.RED,
                ChatColor.GOLD,
                ChatColor.AQUA,
                ChatColor.LIGHT_PURPLE,
                ChatColor.GREEN
        };

        ChatColor randomColor = colors[random.nextInt(colors.length)];

        for (Player p : Bukkit.getOnlinePlayers()) {

            // titre
            p.sendTitle(
                    randomColor + title,
                    ChatColor.GRAY + subtitle,
                    10,
                    80,
                    20
            );

            // son épique
            p.playSound(
                    p.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    1.0f,
                    1.0f
            );
        }

        // message dans le chat
        Bukkit.broadcastMessage(ChatColor.GOLD + chat);

        // feu d'artifice
        spawnFirework(dragon.getLocation());

        // Drop de la tête d'Ender Dragon
        ItemStack dragonHead = new ItemStack(Material.DRAGON_HEAD, 1);
        event.getDrops().add(dragonHead);
    }

    private void spawnFirework(Location loc) {

        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta meta = firework.getFireworkMeta();

        meta.addEffect(
                FireworkEffect.builder()
                        .withColor(Color.PURPLE)
                        .withFade(Color.WHITE)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .trail(true)
                        .flicker(true)
                        .build()
        );

        meta.setPower(1);

        firework.setFireworkMeta(meta);
    }
}