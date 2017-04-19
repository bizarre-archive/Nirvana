package us.ikari.nirvana.game.spectator;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.player.GamePlayer;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

public class GameSpectator {

    private static Nirvana main = Nirvana.getInstance();
    private static Game game = main.getGame();

    @Getter private final Player player;

    public GameSpectator(Player player) {
        this.player = player;

        prepare(player);
    }

    private static void prepare(Player player) {
        player.getInventory().clear();;
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().setArmorContents(null);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.getInventory().setHeldItemSlot(0);;
        player.getInventory().setItem(0, new MenuItemBuilder(Material.COMPASS).name(main.getLangFile().getString("MENU.SPECTATOR.COMPASS.TITLE", LanguageConfigurationFileLocale.EXPLICIT)).lore(main.getLangFile().getStringList("MENU.SPECTATOR.COMPASS.LORE", LanguageConfigurationFileLocale.EXPLICIT)).build().getItemStack());
        player.getInventory().setItem(8, new MenuItemBuilder(Material.INK_SACK).durability(1).name(main.getLangFile().getString("MENU.SPECTATOR.LEAVE.TITLE", LanguageConfigurationFileLocale.EXPLICIT)).lore(main.getLangFile().getStringList("MENU.SPECTATOR.LEAVE.LORE", LanguageConfigurationFileLocale.EXPLICIT)).build().getItemStack());
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);

        for (Player online : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = game.getByPlayer(online);
            if (gamePlayer != null) {
                if (gamePlayer.getData().spectator() == null) {
                    online.hidePlayer(player);
                } else {
                    player.showPlayer(online);
                }
            }
        }

    }

}
