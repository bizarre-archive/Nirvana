package com.veltpvp.nirvana.gamemode.menu;

import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.PlayerMenu;
import us.ikari.phoenix.gui.menu.item.MenuItem;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.Arrays;
import java.util.List;

public class GameMenu extends PlayerMenu {

    private static final String ARROW_RIGHT = ChatColor.GRAY + "» ";
    private static final String ARROW_LEFT = ChatColor.GRAY + " «";

    private static final ItemStack UHC_ITEM = new MenuItemBuilder(Material.GOLDEN_APPLE).durability(1).name(ARROW_RIGHT + "&6&lUHC &d&lSkyWars" + ARROW_LEFT).build().getItemStack();
    private static final ItemStack CLASSIC_ITEM = new MenuItemBuilder(Material.GOLDEN_APPLE).durability(1).name(ARROW_RIGHT + "&c&lClassic &d&lSkyWars" + ARROW_LEFT).build().getItemStack();
    private static final ItemStack POTPVP_ITEM = new MenuItemBuilder(Material.GOLDEN_APPLE).durability(1).name(ARROW_RIGHT + "&e&lPotPvP &d&lSkyWars" + ARROW_LEFT).build().getItemStack();

    public GameMenu(Player player) {
        super(player);
    }

    @Override
    protected void onClose() {
    }

    @Override
    public List<MenuItem> getItems(List<MenuItem> list) {
        List<MenuItem> toReturn = Arrays.asList(new MenuItem[getSize()]);

        int index = 10;
        for (Gamemode gamemode : Gamemode.getGamemodes()) {
            boolean uhc = index == 10;
            boolean classic = index == 13;
            // boolean potpvp = index == 16;

            toReturn.add(index, new MenuItemBuilder(uhc ? UHC_ITEM : classic ? CLASSIC_ITEM : POTPVP_ITEM)
                    .callback(ClickType.LEFT, () -> {
                        player.closeInventory();

                        LobbyProfile profile = LobbyProfile.getByPlayer(player);
                        if (profile.getQueue() != null) {
                            player.sendMessage(ChatColor.RED + "You're already queueing for a game!");
                            return;
                        }

                        gamemode.addToGame(LobbyProfile.getByPlayer(player));
                    })
                    .build());

            index += 3;
        }

        return toReturn;
    }

    @Override
    public String getTitle() {
        return ChatColor.LIGHT_PURPLE + "Game Lobbies";
    }

    @Override
    public int getSize() {
        return 27;
    }
}
