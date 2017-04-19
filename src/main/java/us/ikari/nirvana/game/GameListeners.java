package us.ikari.nirvana.game;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.Nirvana;

public class GameListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    world.setTime(6000);
                }
            }
        }.runTaskTimer(main, 1L, 1L);
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

}
