package com.arkasurvie.dragon;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DragonAnnounce extends JavaPlugin implements Listener {

    private final Random random = new Random();

    // ✅ Cache de la config (évite de relire le fichier YAML à chaque mort)
    private String cachedTitle;
    private String cachedSubtitle;
    private String cachedChat;

    // ✅ Cooldown anti-spam (AtomicLong = thread-safe pour Folia)
    private final AtomicLong lastAnnounce = new AtomicLong(0);
    private static final long COOLDOWN_MS = 10_000L; // 10 secondes

    // Couleurs dans le thème End/Dragon
    private static final List<Color> FIREWORK_COLORS = List.of(
            Color.PURPLE, Color.fromRGB(75, 0, 130),
            Color.WHITE,  Color.fromRGB(148, 0, 211)
    );
    private static final NamedTextColor[] TITLE_COLORS = {
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.WHITE
    };

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadCachedConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DragonAnnounce activé sur Folia !");
    }

    // ✅ Permet un /dragonannounce reload plus tard si besoin
    public void reloadCachedConfig() {
        reloadConfig();
        cachedTitle    = getConfig().getString("title",        "🐉 ENDER DRAGON VAINCU 🐉");
        cachedSubtitle = getConfig().getString("subtitle",     "%player% a tué le dragon !");
        cachedChat     = getConfig().getString("chat-message", "%player% a tué l'Ender Dragon !");
    }

    // ✅ EventPriority.MONITOR = on s'exécute en dernier, sans modifier l'event
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon dragon)) return;

        Player killer = dragon.getKiller();
        if (killer == null) return;

        // ✅ Cooldown thread-safe
        long now = System.currentTimeMillis();
        if (now - lastAnnounce.get() < COOLDOWN_MS) return;
        lastAnnounce.set(now);

        // ✅ Cache local des strings (évite accès répétés)
        final String playerName = killer.getName();
        final String title    = cachedTitle.replace("%player%", playerName);
        final String subtitle = cachedSubtitle.replace("%player%", playerName);
        final String chat     = cachedChat.replace("%player%", playerName);
        final Location dragonLoc = dragon.getLocation().clone();

        // ✅ Couleur aléatoire dans le thème
        final NamedTextColor titleColor = TITLE_COLORS[random.nextInt(TITLE_COLORS.length)];

        // ✅ Folia : annonces globales via GlobalRegionScheduler
        Bukkit.getGlobalRegionScheduler().run(this, scheduledTask -> {

            // Composants Adventure API (remplace ChatColor déprécié)
            Component titleComp = Component.text(title)
                    .color(titleColor)
                    .decorate(TextDecoration.BOLD);
            Component subtitleComp = Component.text(subtitle)
                    .color(NamedTextColor.GRAY);
            Component chatComp = Component.text("🐉 " + chat)
                    .color(NamedTextColor.GOLD);
            Component actionComp = Component.text("« Le End tremble sous vos pieds »")
                    .color(NamedTextColor.DARK_PURPLE)
                    .decorate(TextDecoration.ITALIC);

            Title adventureTitle = Title.title(
                    titleComp,
                    subtitleComp,
                    Title.Times.times(
                            Duration.ofMillis(500),   // fade in
                            Duration.ofSeconds(4),    // stay
                            Duration.ofSeconds(1)     // fade out
                    )
            );

            for (Player p : Bukkit.getOnlinePlayers()) {
                // ✅ Folia : chaque action sur un joueur passe par son scheduler
                p.getScheduler().run(this, t -> {
                    p.showTitle(adventureTitle);
                    p.sendActionBar(actionComp);
                    p.sendMessage(chatComp);

                    // Son de mort du vrai dragon + fanfare
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH,  0.6f, 1.0f);
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                }, null);
            }
        });

        // ✅ Drop tête — safe ici car on est déjà dans l'event thread
        event.getDrops().add(new ItemStack(Material.DRAGON_HEAD, 1));

        // ✅ Folia : feu d'artifice lié à la région du End
        Bukkit.getRegionScheduler().run(this, dragonLoc, t -> {
            spawnFireworkBurst(dragonLoc);
        });
    }

    /**
     * Spawne 4 feux d'artifice en étoile autour de la position du dragon.
     */
    private void spawnFireworkBurst(Location center) {
        double[][] offsets = {{0,0,0}, {3,1,3}, {-3,1,-3}, {3,1,-3}};

        for (double[] offset : offsets) {
            Location loc = center.clone().add(offset[0], offset[1], offset[2]);

            Firework fw = loc.getWorld().spawn(loc, Firework.class);
            FireworkMeta meta = fw.getFireworkMeta();

            Color primary = FIREWORK_COLORS.get(random.nextInt(FIREWORK_COLORS.size()));
            Color fade    = FIREWORK_COLORS.get(random.nextInt(FIREWORK_COLORS.size()));

            FireworkEffect.Type[] types = {
                    FireworkEffect.Type.BALL_LARGE,
                    FireworkEffect.Type.STAR,
                    FireworkEffect.Type.BURST
            };

            meta.addEffect(FireworkEffect.builder()
                    .withColor(primary)
                    .withFade(fade)
                    .with(types[random.nextInt(types.length)])
                    .trail(true)
                    .flicker(true)
                    .build());
            meta.setPower(1);
            fw.setFireworkMeta(meta);
        }
    }
}
