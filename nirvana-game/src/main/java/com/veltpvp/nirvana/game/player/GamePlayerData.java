package com.veltpvp.nirvana.game.player;

import com.veltpvp.nirvana.game.kit.GameKit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import com.veltpvp.nirvana.game.spectator.GameSpectator;

@Accessors(chain = true, fluent = true)
public class GamePlayerData {

    @Getter @Setter private int kills;
    @Getter @Setter private GameKit kit;
    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private boolean alive = true;
    @Getter @Setter private boolean won;
    @Getter @Setter private GameSpectator spectator;
    @Getter @Setter private boolean sending;

}
