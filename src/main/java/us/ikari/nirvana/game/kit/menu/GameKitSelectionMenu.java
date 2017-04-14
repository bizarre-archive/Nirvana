package us.ikari.nirvana.game.kit.menu;

import org.bukkit.entity.Player;
import us.ikari.phoenix.gui.menu.PlayerMenu;
import us.ikari.phoenix.gui.menu.item.MenuItem;

import java.util.List;

public class GameKitSelectionMenu extends PlayerMenu {

    private static final int SIZE = 32;

    public GameKitSelectionMenu(Player player) {
        super(player, 32, );
    }

    @Override
    public List<MenuItem> getItems() {
        return null;
    }

}
