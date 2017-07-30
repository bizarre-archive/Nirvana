package com.veltpvp.nirvana.bukkit.game.player;

import com.veltpvp.nirvana.bukkit.game.kit.GameKit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import com.veltpvp.nirvana.bukkit.game.spectator.GameSpectator;

@Accessors(chain = true, fluent = true)
public class GamePlayerData {

    @Getter @Setter private int kills;
    @Getter @Setter private GameKit kit;
    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private boolean alive = true;
    @Getter @Setter private GameSpectator spectator;

}
