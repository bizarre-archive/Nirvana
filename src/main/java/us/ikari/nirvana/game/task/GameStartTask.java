package us.ikari.nirvana.game.task;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameEventStage;
import us.ikari.nirvana.game.GameState;
import us.ikari.nirvana.game.chest.GameChest;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.nirvana.game.player.GamePlayer;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.Random;

public class GameStartTask extends BukkitRunnable {

    private static final long DEFAULT_DURATION = Nirvana.getInstance().getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN");

    @Getter private final Game game;
    @Getter private long duration;
    @Getter private long time;

    public GameStartTask(Game game, long duration) {
        this.game = game;
        this.duration = duration;
        this.time = System.currentTimeMillis();

        runTaskLater(Nirvana.getInstance(), (duration / 1000) * 20);
    }

    public GameStartTask(Game game) {
        this(game, DEFAULT_DURATION);
    }

    @Override
    public void run() {
        HandlerList.unregisterAll(game.getLobby().getListeners());
        game.setState(GameState.PLAY);
        game.getGameTime().reset();

        for (GamePlayer gamePlayer : game.getPlayers()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUuid());

            if (player != null) {
                if (gamePlayer.getData().alive()) {
                    player.getInventory().clear();
                }

                for (String message : Nirvana.getInstance().getLangFile().getStringList("GAME.START", LanguageConfigurationFileLocale.EXPLICIT)) {
                    player.sendMessage(message);
                }

                if (gamePlayer.getData().alive()) {
                    Entity vehicle = player.getVehicle();
                    if (vehicle != null) {
                        player.leaveVehicle();
                        vehicle.remove();
                    }

                    GameKit kit = gamePlayer.getData().kit();
                    if (kit != null) {
                        ItemStack[] armor = kit.getArmor();
                        ArrayUtils.reverse(armor);

                        player.getInventory().addItem(kit.getContents());
                        player.getInventory().setArmorContents(armor);
                    }
                }
            }

        }

        for (GameEventStage stage : GameEventStage.values()) {
            if (stage != GameEventStage.NONE) {
               new BukkitRunnable() {
                   @Override
                   public void run() {
                       game.setRefillStage(stage);
                       if (stage != GameEventStage.DEATHMATCH) {

                           GameChest.getLoadedChests().clear();
                           if (stage == GameEventStage.SECOND_REFILL) {

                               for (Location chest : GameChest.BASIC.getInstances()) {
                                   GameChest.BUFFED.getInstances().add(chest);
                               }

                               GameChest.BASIC.getInstances().clear();
                           }

                           for (Player player : Bukkit.getOnlinePlayers()) {
                               player.sendMessage(Nirvana.getInstance().getLangFile().getString("GAME.EVENT.REFILL", LanguageConfigurationFileLocale.EXPLICIT));
                           }
                       } else {
                           new BukkitRunnable() {
                               @Override
                               public void run() {
                                   for (GamePlayer gamePlayer : game.getPlayers()) {
                                       if (gamePlayer.getData().alive()) {
                                           Player player = Bukkit.getPlayer(gamePlayer.getName());
                                           if (player != null) {
                                               TNTPrimed tnt = player.getWorld().spawn(new Location(player.getWorld(), (new Random().nextInt(10) - 5 + player.getLocation().getBlockX()), player.getLocation().getBlockY() + 50, new Random().nextInt(10) - 5 + player.getLocation().getBlockZ()), TNTPrimed.class);
                                               tnt.setFuseTicks(120);
                                           }
                                       }
                                   }
                               }
                           }.runTaskTimer(Nirvana.getInstance(), 0, (Nirvana.getInstance().getConfigFile().getInteger("STATE.EVENT.DEATHMATCH.PERIOD") / 1000) * 20);
                       }
                   }
               }.runTaskLater(Nirvana.getInstance(), ((GameEventStage.getDuration(stage) + GameEventStage.getCountdown(stage)) / 1000) * 20);
            }
        }
    }

}
