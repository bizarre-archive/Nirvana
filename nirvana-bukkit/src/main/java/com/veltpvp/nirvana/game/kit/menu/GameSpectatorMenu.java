package com.veltpvp.nirvana.game.kit.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.Game;
import com.veltpvp.nirvana.game.player.GamePlayer;
import us.ikari.phoenix.gui.menu.PlayerMenu;
import us.ikari.phoenix.gui.menu.item.MenuItem;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.List;

public class GameSpectatorMenu extends PlayerMenu {

    private static final Nirvana main = Nirvana.getInstance();
    private static final Game game = main.getGame();

    public GameSpectatorMenu(Player player) {
        super(player, getInventorySize(), main.getLangFile().getString("MENU.SPECTATOR.TITLE", LanguageConfigurationFileLocale.ENGLISH)); //TODO CHANGE
    }

    @Override
    public List<MenuItem> getItems(List<MenuItem> items) {

        for (GamePlayer gamePlayer : game.getAlivePlayers()) {
            Player otherPlayer = Bukkit.getPlayer(gamePlayer.getName());

            if (otherPlayer != null) {
                items.add(new MenuItemBuilder(Material.SKULL_ITEM).durability(3).name(otherPlayer.getDisplayName()).callback(ClickType.LEFT, new Runnable() {
                    @Override
                    public void run() {
                    player.teleport(otherPlayer.getLocation());

                    }
                }).build());
            }
        }

        return items;
    }


    @Override
    protected void onClose() {

    }

    private static int getInventorySize() {
        return (int) (Math.ceil(game.getAlivePlayers().size() / 9.0) * 9);
    }

}
