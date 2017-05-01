package us.ikari.nirvana.game.spectator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameState;
import us.ikari.nirvana.game.kit.menu.GameSpectatorMenu;
import us.ikari.nirvana.game.player.GamePlayer;

public class GameSpectatorListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameSpectatorListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
            event.setCancelled(true);

            for (GamePlayer other : game.getPlayers()) {
                if (other.getData().spectator() != null || game.getState() == GameState.END) {
                    Player otherPlayer = Bukkit.getPlayer(other.getUuid());
                    if (otherPlayer != null) {
                        otherPlayer.sendMessage(ChatColor.GRAY + player.getName() + ": " + event.getMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            GamePlayer gamePlayer = game.getByPlayer(player);

            if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
            event.setCancelled(true);

            ItemStack itemStack = event.getItem();

            if (itemStack != null) {

                if (itemStack.getType() == Material.COMPASS) {
                    player.openInventory(new GameSpectatorMenu(player).getInventory());
                    return;
                }

                if (itemStack.getType() == Material.INK_SACK) {
                    player.kickPlayer("");
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            GamePlayer gamePlayer = game.getByPlayer(player);

            if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
                event.setCancelled(true);

                if (player.getLocation().getBlockY() <= 0) {
                    player.teleport(player.getWorld().getSpawnLocation());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
            event.setCancelled(true);
        }
    }
}
