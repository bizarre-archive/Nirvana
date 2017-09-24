package com.veltpvp.nirvana.game.kit.ability;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.Game;
import com.veltpvp.nirvana.game.kit.GameKit;
import com.veltpvp.nirvana.game.kit.PowerfulGameKit;
import com.veltpvp.nirvana.game.player.GamePlayer;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class GameKitAbilityListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameKitAbilityListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer != null) {
            GamePlayer gamePlayer = game.getByPlayer(killer);
            if (gamePlayer != null) {
                GameKit kit = gamePlayer.getData().kit();
                if (kit != null && kit instanceof PowerfulGameKit) {
                    PowerfulGameKit powerfulGameKit = (PowerfulGameKit) kit;
                    if (powerfulGameKit.getAbilities().contains(GameKitAbility.PEARL_ON_KILL)) {
                        killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                    }
                }
            }
        }

        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null) {
            GameKit kit = gamePlayer.getData().kit();
            if (kit != null && kit instanceof PowerfulGameKit) {
                PowerfulGameKit powerfulGameKit = (PowerfulGameKit) kit;
                if (powerfulGameKit.getAbilities().contains(GameKitAbility.EXPLODE_ON_DEATH) && main.getLocalNirvanaServer().getType() != NirvanaServerType.UHC) {
                    TNTPrimed tnt = player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
                    tnt.setFuseTicks(40);
                }
            }
        }
    }

}
