package com.veltpvp.nirvana.lobby;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.packet.lobby.LobbyServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import us.ikari.phoenix.gui.menu.PlayerMenu;
import us.ikari.phoenix.gui.menu.item.MenuItem;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class LobbyMenu extends PlayerMenu {

    @Getter @Setter private List<LobbyServer> lobbies;

    public LobbyMenu(Player player, List<LobbyServer> lobbies) {
        super(player);

        this.lobbies = lobbies;
    }

    @Override
    protected void onClose() {

    }

    @Override
    public List<MenuItem> getItems(List<MenuItem> list) {
        List<MenuItem> toReturn = Arrays.asList(new MenuItem[getSize()]);

        lobbies.sort(Comparator.comparing(LobbyServer::getId));

        for (int i = 0; i < lobbies.size(); i++) {
            LobbyServer lobby = lobbies.get(i);

            toReturn.set(i, new MenuItemBuilder(Nirvana.getInstance().getId().equalsIgnoreCase(lobby.getId()) ? Material.PAPER : Material.EMPTY_MAP).name(ChatColor.LIGHT_PURPLE + lobby.getId().toUpperCase() + (Nirvana.getInstance().getId().equalsIgnoreCase(lobby.getId()) ? ChatColor.GRAY + " (Current)" : ChatColor.GRAY + " (Click to join)")).lore(
                    ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------",
                    ChatColor.YELLOW + "Players: " + ChatColor.WHITE + lobby.getPlayers() + ChatColor.GRAY + " / " + ChatColor.WHITE + lobby.getMaxPlayers(),
                    ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------"
            ).build().setCallback(ClickType.LEFT, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();

                    if (lobby.getId().equalsIgnoreCase(Nirvana.getInstance().getId())) {
                        player.sendMessage(ChatColor.RED + "You're already connected to this lobby.");
                    } else {
                        if (lobby.getPlayers() >= lobby.getMaxPlayers()) {
                            player.sendMessage(ChatColor.RED + "This lobby is currently full!");
                            return;
                        }

                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(byteArray);

                        try {
                            out.writeUTF("Connect");
                            out.writeUTF(lobby.getId());
                        } catch (IOException ex) {
                            player.sendMessage(ChatColor.RED + "An error occurred.");
                            return;
                        }

                        player.sendPluginMessage(Nirvana.getInstance(), "BungeeCord", byteArray.toByteArray());
                    }
                }
            }));
        }

        return toReturn;
    }

    @Override
    public String getTitle() {
        return ChatColor.LIGHT_PURPLE + "SkyWars Lobbies";
    }

    @Override
    public int getSize() {
        int size = (int) Math.ceil(lobbies.size() / 9.0);

        if (size == 0) {
            size = 1;
        }

        return size * 9;
    }
}
