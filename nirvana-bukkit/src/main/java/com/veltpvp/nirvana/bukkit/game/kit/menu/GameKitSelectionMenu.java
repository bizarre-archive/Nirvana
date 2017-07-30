package com.veltpvp.nirvana.bukkit.game.kit.menu;

import com.veltpvp.nirvana.bukkit.Nirvana;
import com.veltpvp.nirvana.bukkit.game.Game;
import com.veltpvp.nirvana.bukkit.game.kit.GameKit;
import com.veltpvp.nirvana.bukkit.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.PlayerMenu;
import us.ikari.phoenix.gui.menu.item.MenuItem;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.List;

public class GameKitSelectionMenu extends PlayerMenu {

    private static final Nirvana main = Nirvana.getInstance();
    private static final Game game = main.getGame();

    private GamePlayer gamePlayer;

    public GameKitSelectionMenu(Player player, GamePlayer gamePlayer) {
        super(player, getInventorySize(), main.getLangFile().getString("MENU.KIT_SELECTION.TITLE", LanguageConfigurationFileLocale.ENGLISH)); //TODO CHANGE

        this.gamePlayer = gamePlayer;
    }

    @Override
    public List<MenuItem> getItems(List<MenuItem> items) {
        for (GameKit kit : game.getKits()) {
            if (GameKit.canUse(player, kit)) {
                items.add(getItem(kit, LanguageConfigurationFileLocale.ENGLISH)); //TODO: change
            }
        }

        for (GameKit kit : game.getKits()) {
            if (!GameKit.canUse(player, kit)) {
                items.add(getItem(kit, LanguageConfigurationFileLocale.ENGLISH)); //TODO: change
            }
        }
        return items;
    }

    private MenuItem getItem(GameKit kit, LanguageConfigurationFileLocale locale) {
        String key = GameKit.canUse(player, kit) ? "UNLOCKED" : "LOCKED";

        boolean unlocked = key.equals("UNLOCKED");

        return new MenuItemBuilder(unlocked ? kit.getIcon() : new ItemStack(Material.STAINED_GLASS_PANE))
                .name(main.getLangFile().getString("MENU.KIT_SELECTION." + key + ".TITLE", locale, GameKit.getDisplayName(kit, locale)))
                .lore(main.getLangFile().getStringList("MENU.KIT_SELECTION." + key + ".LORE", locale, GameKit.getDescription(kit, locale)))
                .durability(unlocked ? kit.getIcon().getDurability() : 14)
                .callback(ClickType.LEFT, new Runnable() {
                    @Override
                    public void run() {
                        if (unlocked) {
                            gamePlayer.getData().kit(kit);
                            player.sendMessage(main.getLangFile().getString("MENU.KIT_SELECTION.SELECTED", locale, GameKit.getDisplayName(kit, locale)));
                        } else {
                            player.sendMessage(main.getLangFile().getString("MENU.KIT_SELECTION.LOCKED.MESSAGE", locale, GameKit.getDisplayName(kit, locale)));
                        }
                        player.closeInventory();
                    }
                })
                .build();
    }

    @Override
    protected void onClose() {

    }

    private static int getInventorySize() {
        return (int) (Math.ceil(game.getKits().size() / 9.0) * 9);
    }

}
