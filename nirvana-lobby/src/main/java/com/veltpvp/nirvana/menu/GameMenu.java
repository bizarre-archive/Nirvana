package com.veltpvp.nirvana.menu;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.gamemode.Gamemode;
import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;
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

    private static final int INVENTORY_SIZE = 27;
    private static final String LEFT_ARROW = StringEscapeUtils.unescapeHtml3("&laquo;");
    private static final String RIGHT_ARROW = StringEscapeUtils.unescapeHtml3("&raquo;");

    public GameMenu(Player player) {
        super(player, INVENTORY_SIZE, ChatColor.LIGHT_PURPLE + "Game Selector");
    }

    @Override
    protected void onClose() {

    }

    @Override
    public List<MenuItem> getItems(List<MenuItem> list) {
        List<MenuItem> toReturn = Arrays.asList(new MenuItem[getSize()]);

        for (int i = 0; i < Gamemode.getGamemodes().size(); i++) {
            int index = 7 + ((i+1) * 3);
            Gamemode gamemode = Gamemode.getGamemodes().get(i);

            toReturn.set(index, getGamemodeMenuItem(gamemode));
        }

        for (int i = 0; i < toReturn.size(); i++) {
            MenuItem item = toReturn.get(i);
            if (item == null) {
                toReturn.set(i, new MenuItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(" ").build());
            }
        }

        return toReturn;
    }

    private MenuItem getGamemodeMenuItem(Gamemode gamemode) {
        ItemStack item = getGamemodeItemStack(gamemode);

        return new MenuItemBuilder(item).name(getGamemodeItemStackName(gamemode)).callback(ClickType.LEFT, new Runnable() {
            @Override
            public void run() {
                Nirvana.getInstance().getLobby().queue(player, gamemode);
                player.closeInventory();
            }
        }).build();
    }

    private String getGamemodeItemStackName(Gamemode gamemode) {
        ChatColor[] colors = new ChatColor[]{ChatColor.GOLD, ChatColor.RED, ChatColor.YELLOW};

        String part = ChatColor.WHITE + "" + ChatColor.BOLD + gamemode.getName();
        for (int i = 0; i < Gamemode.getGamemodes().size(); i++) {
            if (i >= Gamemode.getGamemodes().size()) continue;
            if (i >= colors.length) continue;
            Gamemode other = Gamemode.getGamemodes().get(i);

            if (other.equals(gamemode)) {
                part = colors[i] + "" + ChatColor.BOLD + gamemode.getName();
                break;
            }

        }

        return String.format(ChatColor.GRAY + "" + ChatColor.BOLD + RIGHT_ARROW + ChatColor.RESET + " %s " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "SkyWars " + ChatColor.GRAY + ChatColor.BOLD + LEFT_ARROW, part);
    }

    private ItemStack getGamemodeItemStack(Gamemode gamemode) {
        if (gamemode.getId().equalsIgnoreCase("potpvp")) {
            return new ItemStack(Material.POTION, 1, (short) 16421);
        } else if (gamemode.getId().equalsIgnoreCase("uhc")) {
            return new ItemStack(Material.GOLDEN_APPLE, 1, (short) 2);
        } else {
            return new ItemStack(Material.STONE_SWORD);
        }
    }
}
