package us.ikari.nirvana.game.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameState;

public class GameLobbyListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameLobbyListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }


    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (game.getState() == GameState.LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (game.getState() == GameState.LOBBY) {
            event.setCancelled(true);
        }
    }


}
