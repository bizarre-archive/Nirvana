package us.ikari.nirvana.game.player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import us.ikari.nirvana.game.kit.GameKit;

@Accessors(chain = true, fluent = true)
public class GamePlayerData {

    @Getter @Setter private int kills;
    @Getter @Setter private GameKit kit;
    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private boolean alive = true;

}
