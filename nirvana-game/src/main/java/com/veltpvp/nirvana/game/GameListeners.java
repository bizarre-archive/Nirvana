package com.veltpvp.nirvana.game;

import com.veltpvp.nirvana.Nirvana;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

public class GameListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DEFAULT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                double health = (((Player) event.getEntity()).getHealth() - event.getFinalDamage()) / 2;
                if (health > 0) {
                    ((Player) arrow.getShooter()).sendMessage(main.getLangFile().getString("GAME.HIT", LanguageConfigurationFileLocale.ENGLISH, ((Player) event.getEntity()).getDisplayName(), (Math.round(health * 2) / 2.0)));
                }
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("HydrogenPrefix")) {
            String prefix = player.getMetadata("HydrogenPrefix").get(0).asString();
            event.setFormat(prefix + "%s: %s");
        } else {
            event.setFormat("%s: %s");
        }
    }

}
