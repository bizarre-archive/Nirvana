package com.veltpvp.nirvana.lobby;

import com.veltpvp.nirvana.Nirvana;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

//TODO: make configurable
public class LobbyItems {

    private static Nirvana main = Nirvana.getInstance();

    public static final ItemStack TOGGLE_VISIBILITY_OFF_ITEM = getTogglePlayersOffItem();
    public static final ItemStack TOGGLE_VISIBILITY_ON_ITEM = getTogglePlayersOnItem();
    public static final ItemStack INFORMATION_BOOK = getInformationBook();
    public static final ItemStack LOBBY_SELECTOR = getLobbySelector();

    private static ItemStack getTogglePlayersOffItem() {
        ItemStack toReturn = new MenuItemBuilder(Material.INK_SACK).durability(8).name(ChatColor.GREEN + "Toggle Visibility").build().getItemStack();

        return toReturn;
    }

    private static ItemStack getTogglePlayersOnItem() {
        ItemStack toReturn = new MenuItemBuilder(Material.INK_SACK).durability(10).name(ChatColor.GRAY + "Toggle Visibility").build().getItemStack();

        return toReturn;
    }

    //TODO: Fill this
    private static ItemStack getInformationBook() {
        ItemStack toReturn = new MenuItemBuilder(Material.WRITTEN_BOOK).name(ChatColor.GOLD + "Welcome to SkyWars!").build().getItemStack();

        BookMeta meta = (BookMeta) toReturn.getItemMeta();

        meta.setAuthor("VeltPvP Administration Team");
        meta.addPage(ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome to " + (ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD  +"VeltPvP") + ChatColor.GOLD + "" + ChatColor.BOLD + " SkyWars!");

        toReturn.setItemMeta(meta);

        return toReturn;
    }

    private static ItemStack getLobbySelector() {
        ItemStack toReturn = new MenuItemBuilder(Material.NETHER_STAR).name(ChatColor.YELLOW + "Lobby Selector").build().getItemStack();

        return toReturn;
    }

}
