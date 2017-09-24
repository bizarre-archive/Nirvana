package com.veltpvp.nirvana.game.player;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.Game;
import com.veltpvp.nirvana.game.GameState;
import com.veltpvp.nirvana.game.spectator.GameSpectator;
import com.veltpvp.nirvana.game.task.GameStartTask;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerInfoPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.TileEntityChest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;
import us.ikari.phoenix.scoreboard.scoreboard.Board;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardCooldown;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayerListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GamePlayerListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            if (gamePlayer == null) {
                if (game.getState() != GameState.LOBBY) {
                    event.setKickMessage("Game has already started.");
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    return;
                }

                gamePlayer = new GamePlayer(player.getUniqueId(), player.getName(), player.getDisplayName());
                if (game.getPlayers().size() >= game.getLobby().getSpawnLocations().size()) {
                    gamePlayer.getData().alive(false);
                    gamePlayer.getData().deathTime(System.currentTimeMillis());
                }

                game.getPlayers().add(gamePlayer);
            }
        }

    }

    @EventHandler
    public void onPlayerHealthChangeEvent(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (main.getLocalNirvanaServer().getType() == NirvanaServerType.UHC && event.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM && !event.getRegainReason().name().contains("REGEN")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathTimebombEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getLocation().getBlockY() <= 0) {
            return;
        }

        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer == null || gamePlayer.getData().spectator() != null) {
            return;
        }

        if (main.getLocalNirvanaServer().getType() == NirvanaServerType.UHC) {
            List<ItemStack> drops = new ArrayList<>(event.getDrops());
            event.getDrops().clear();

            Block block = player.getLocation().getBlock();

            if (game.getState() == GameState.PLAY) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "[" + (ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "TimeBomb" + ChatColor.RESET) + ChatColor.GRAY + "] " + (ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GRAY)
                        + (player.getName().endsWith("s") || player.getName().endsWith("z") ? "'" : "'s") + " corpse will explode in " + ChatColor.LIGHT_PURPLE + "10 seconds" + ChatColor.GRAY + "!");

            }
            block.setType(Material.CHEST);

            Chest chest = (Chest) block.getState();

            for (ItemStack itemStack : drops) {
                chest.getBlockInventory().addItem(itemStack);
            }

            chest.getBlockInventory().addItem(new MenuItemBuilder(Material.GOLDEN_APPLE).name(ChatColor.GOLD + "" + ChatColor.BOLD + "Golden Head").build().getItemStack());

            TileEntityChest entity = (TileEntityChest) ((CraftWorld) chest.getWorld()).getHandle().getTileEntity(chest.getLocation().getBlockX(), chest.getLocation().getBlockY(), chest.getLocation().getBlockZ());
            entity.a(ChatColor.GOLD + player.getName() + (player.getName().endsWith("s") || player.getName().endsWith("z") ? "'" : "'s") + " Corpse");

            Block finalBlock = block;
            new BukkitRunnable() {
                @Override
                public void run() {
                    chest.getWorld().createExplosion(chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(), 7.5F, false, false);
                    chest.getBlockInventory().clear();
                    finalBlock.setType(Material.AIR);
                }
            }.runTaskLater(main, 20 * 10L);

            if (drops.size() > 27) {

            }
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack != null) { // can it be null? doubt it but idc i dont wanna restart
            if (itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(ChatColor.GOLD.toString())) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1), true);
            }
        }

    }

    @EventHandler
    public void onPlayerInteractPearlEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (main.getLocalNirvanaServer().getType() != NirvanaServerType.POTPVP) {
            return;
        }

        if (itemStack != null && itemStack.getType() == Material.ENDER_PEARL && event.getAction() == Action.RIGHT_CLICK_AIR) {
            Board board = Board.getByPlayer(player);

            if (board != null) {
                BoardCooldown cooldown = board.getCooldown("pearl");

                if (cooldown != null) {
                    player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.YELLOW + cooldown.getFormattedString(BoardFormat.SECONDS) + "s" + ChatColor.RED + " before pearling again!");
                    player.updateInventory();
                    event.setCancelled(true);
                    return;
                }

                new BoardCooldown(board, "pearl", 6);
            }

        }

    }

    @EventHandler
    public void onPlayerDeathMessageEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        GamePlayer deathPlayer = game.getByPlayer(player);

        event.setDeathMessage(null);

        if (deathPlayer == null) {
            return;
        }

        Player killer = player.getKiller();
        if (killer != null) {

            GamePlayer gamePlayer = game.getByPlayer(killer);
            if (gamePlayer == null) {
                return;
            }

            gamePlayer.getData().kills(gamePlayer.getData().kills() + 1);

            if (gamePlayer.getData().alive()) {
                double health = killer.getHealth() / 2;
                player.sendMessage(main.getLangFile().getString("GAME.HEALTH", LanguageConfigurationFileLocale.ENGLISH, killer.getDisplayName(), (Math.round(health * 2) / 2.0)));
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(main.getLangFile().getString("DEATH_MESSAGE.PLAYER", LanguageConfigurationFileLocale.ENGLISH, player.getDisplayName(), deathPlayer.getData().kills(), killer.getDisplayName(), gamePlayer.getData().kills()));
            }

        } else {
            EntityDamageEvent.DamageCause cause;
            if (player.getLastDamageCause() == null) {
                cause = EntityDamageEvent.DamageCause.CUSTOM;
            } else {
                cause = player.getLastDamageCause().getCause();
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                String message;
                if (cause == null) {
                    message = main.getLangFile().getString("DEATH_MESSAGE.CUSTOM", LanguageConfigurationFileLocale.ENGLISH, player.getDisplayName(), deathPlayer.getData().kills());
                } else {
                    message = main.getLangFile().getString("DEATH_MESSAGE." + cause.name().toUpperCase(), LanguageConfigurationFileLocale.ENGLISH, player.getDisplayName(), deathPlayer.getData().kills());
                    if (message == null) {
                        message = main.getLangFile().getString("DEATH_MESSAGE.CUSTOM", LanguageConfigurationFileLocale.ENGLISH, player.getDisplayName(), deathPlayer.getData().kills());
                    }
                }

                online.sendMessage(message);
            }

        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (game.shouldStart()) {
            game.getActiveTasks().add(new GameStartTask(game));
            game.getGameTime().reset();
        }

        //TODO: do async
        main.getLocalNirvanaServer().setPlayers(Bukkit.getOnlinePlayers().size());
        main.getLocalNirvanaServer().setMaxPlayers(Bukkit.getMaxPlayers());
        main.getNetwork().sendPacket(new ServerInfoPacket(main.getLocalNirvanaServer()), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);

        event.setJoinMessage(null);

        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer != null && !gamePlayer.getData().alive()) {
            gamePlayer.getData().spectator(new GameSpectator(player));
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack != null && itemStack.getType() == Material.TNT && event.getAction().name().contains("LEFT")) {

            Board board = Board.getByPlayer(player);
            if (board != null) {
                BoardCooldown cooldown = board.getCooldown("bomb");

                if (cooldown != null) {
                    player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.YELLOW + cooldown.getFormattedString(BoardFormat.SECONDS) + "s" + ChatColor.RED + " before bombarding again!");
                    return;
                }

                if (itemStack.getAmount() > 1) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                } else {
                    player.setItemInHand(new ItemStack(Material.AIR));
                }

                TNTPrimed primed = player.getWorld().spawn(player.getLocation().clone().add(0, 2, 0), TNTPrimed.class);

                primed.setFuseTicks(25);
                primed.setVelocity(player.getLocation().getDirection().multiply(0.85));

                new BoardCooldown(board, "bomb", 5);
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (game.getState() != GameState.LOBBY) {
            Block block = event.getBlock();
            if (block.getType() == Material.TNT) {

                Board board = Board.getByPlayer(event.getPlayer());

                if (board.getCooldown("bomb") != null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You must wait " + ChatColor.YELLOW + board.getCooldown("bomb").getFormattedString(BoardFormat.SECONDS) + "s" + ChatColor.RED + " before bombarding again!");
                    event.setCancelled(true);
                    return;
                }

                block.setType(Material.AIR);
                TNTPrimed tnt = block.getWorld().spawn(block.getLocation().clone().add(0.5, 1.5, 0.5), TNTPrimed.class);
                tnt.setFuseTicks(15);

                new BoardCooldown(board, "bomb", 5);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        GamePlayer gamePlayer = game.getByPlayer(event.getPlayer());

        if (gamePlayer != null) {
            gamePlayer.getData().spawnLocation(null);
            gamePlayer.getData().alive(false);
            gamePlayer.getData().deathTime(System.currentTimeMillis());
        }

        event.setQuitMessage(null);

        //TODO: do async
        main.getLocalNirvanaServer().setPlayers(Bukkit.getOnlinePlayers().size() - 1);
        main.getLocalNirvanaServer().setMaxPlayers(Bukkit.getMaxPlayers());
        main.getNetwork().sendPacket(new ServerInfoPacket(main.getLocalNirvanaServer()), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);

        if (game.getState() != GameState.LOBBY) {
            event.setQuitMessage(null);

            if (gamePlayer != null && gamePlayer.getData().spectator() == null && game.getState() != GameState.END) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.sendMessage(main.getLangFile().getString("GAME.QUIT", LanguageConfigurationFileLocale.ENGLISH, event.getPlayer().getDisplayName()));
                }
            }

            if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
                return;
            }

            if (game.getState() != GameState.END) {
                event.getPlayer().setHealth(0);
            }
        }
        //not going to remove because we want to save all participating players later
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (event.getFoodLevel() < player.getFoodLevel()) {
            if (new Random().nextInt(3) == 1) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null) {

            gamePlayer.getData().alive(false);
            gamePlayer.getData().deathTime(System.currentTimeMillis());
            player.setHealth(player.getMaxHealth());

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setFoodLevel(20);
                    gamePlayer.getData().spectator(new GameSpectator(player));
                    if (player.getLocation().getBlockY() <= 0) {
                        if (player.getKiller() != null) {
                            player.teleport(player.getKiller().getLocation());
                        } else {
                            player.teleport(player.getWorld().getHighestBlockAt(player.getWorld().getSpawnLocation()).getLocation());
                        }
                    }
                }
            }.runTaskLater(main, 2L);

            game.update();
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            Player player = (Player) event.getEntity();
            GamePlayer gamePlayer = game.getByPlayer(player);

            if (gamePlayer != null && gamePlayer.getData().alive()) {
                event.setCancelled(true);
                player.damage(player.getMaxHealth());
            }

        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null) {
            gamePlayer.getData().spectator(new GameSpectator(player));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEventScoreboard(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                Scoreboard scoreboard = player.getScoreboard();
                if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                    return;
                }

                Objective objective = scoreboard.registerNewObjective("health", "health");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(ChatColor.DARK_RED + "\u2764");
            }
        }.runTaskLater(main, 20L);
    }

}
