package com.veltpvp.nirvana.lobby;

import com.veltpvp.nirvana.Nirvana;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

//TODO: make configurable
public class LobbyItems {

    private static Nirvana main = Nirvana.getInstance();

    public static final ItemStack TOGGLE_VISIBILITY_OFF_ITEM = getTogglePlayersOffItem();
    public static final ItemStack TOGGLE_VISIBILITY_ON_ITEM = getTogglePlayersOnItem();
    public static final ItemStack INFORMATION_BOOK = getInformationBook();
    //public static final ItemStack LOBBY_SELECTOR = getLobbySelector();
    public static final ItemStack PARTY_CREATOR = getPartyCreator();
    public static final ItemStack PARTY_DISBANDER = getPartyDisbander();

    private static ItemStack getTogglePlayersOffItem() {
        ItemStack toReturn = new MenuItemBuilder(Material.INK_SACK).durability(8).name(ChatColor.GRAY + "Toggle Visibility").build().getItemStack();

        return toReturn;
    }

    private static ItemStack getTogglePlayersOnItem() {
        ItemStack toReturn = new MenuItemBuilder(Material.INK_SACK).durability(10).name(ChatColor.GREEN + "Toggle Visibility").build().getItemStack();

        return toReturn;
    }

    //TODO: Fill this
    private static ItemStack getInformationBook() {
        ItemStack toReturn = new MenuItemBuilder(Material.BOOK).name(ChatColor.LIGHT_PURPLE + "Game Selector").build().getItemStack();

        ItemMeta meta = toReturn.getItemMeta();
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        toReturn.setItemMeta(meta);
        toReturn.removeEnchantment(Enchantment.KNOCKBACK);

        return toReturn;
    }

    private static ItemStack getLobbySelector() {
        ItemStack toReturn = new MenuItemBuilder(Material.NETHER_STAR).name(ChatColor.YELLOW + "Lobby Selector").build().getItemStack();

        return toReturn;
    }

    private static ItemStack getPartyCreator() {
        ItemStack toReturn = new MenuItemBuilder(Material.NETHER_STAR).name(ChatColor.YELLOW + "Create A Party").build().getItemStack();

        return toReturn;
    }

    private static ItemStack getPartyDisbander() {
        ItemStack toReturn = new MenuItemBuilder(Material.FIRE).name(ChatColor.YELLOW + "Disband Your Party").build().getItemStack();

        return toReturn;
    }

}
