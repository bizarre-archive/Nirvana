package com.veltpvp.nirvana.gamemode.menu;

import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GameButton extends Button {

    private Gamemode gamemode;

    public GameButton(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return gamemode.getItem();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        player.closeInventory();

        if (profile.getQueue() != null) {
            player.sendMessage(ChatColor.RED + "You're already queueing for a game!");
            return;
        }

        gamemode.addToGame(profile);
    }
}
