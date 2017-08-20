package com.veltpvp.nirvana.lobby;

import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lobby {

    @Getter private final Location spawnLocation;

    public Lobby(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setupPlayer(Player player, LobbyProfile profile) {
        player.teleport(spawnLocation);

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setWalkSpeed(0.4F);

        player.getInventory().clear();

        player.getInventory().setItem(0, LobbyItems.INFORMATION_BOOK);
        player.getInventory().setItem(1, profile.isHidePlayers() ? LobbyItems.TOGGLE_VISIBILITY_ON_ITEM : LobbyItems.TOGGLE_VISIBILITY_OFF_ITEM);
        player.getInventory().setItem(8, LobbyItems.LOBBY_SELECTOR);
    }


}
