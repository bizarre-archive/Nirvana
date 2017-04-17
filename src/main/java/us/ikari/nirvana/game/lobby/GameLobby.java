package us.ikari.nirvana.game.lobby;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.List;

public class GameLobby {

    private static Nirvana main = Nirvana.getInstance();

    @Getter private final List<Location> spawnLocations;
    @Getter private final GameLobbyListeners listeners;

    public GameLobby(Game game, List<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
        this.listeners = new GameLobbyListeners(Nirvana.getInstance(), game);

        Bukkit.getLogger().info("Lobby registered " + spawnLocations.size() + " spawn locations.");
    }


    public static ItemStack getKitSelector(LanguageConfigurationFileLocale locale) {
        return new MenuItemBuilder(Material.ENCHANTED_BOOK)
                .name(main.getLangFile().getString("LOBBY.KIT_SELECTION_ITEM.NAME", locale))
                .lore(main.getLangFile().getStringList("LOBBY.KIT_SELECTION_ITEM.LORE", locale))
                .build()
                .getItemStack();
    }

    public void prepare(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(0, getKitSelector(LanguageConfigurationFileLocale.EXPLICIT)); //TODO Change
    }

}
