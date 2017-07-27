package com.veltpvp.nirvana.game.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.GameUtils;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.ArrayList;
import java.util.List;

public interface GameKit {

    ItemStack[] getContents();
    ItemStack[] getArmor();
    String getIdentifier();
    ItemStack getIcon();

    static boolean canUse(Player player, GameKit kit) {
        return !(kit instanceof PermissibleGameKit) || player.hasPermission(((PermissibleGameKit) kit).getPermission()) || player.hasPermission("kit.*");
    }

    static List<String> getDescription(GameKit kit, LanguageConfigurationFileLocale locale) {
       return GameUtils.recursiveSplitString(Nirvana.getInstance().getLangFile().getString("KIT." + kit.getIdentifier().toUpperCase() + ".DESCRIPTION", locale), new ArrayList<>(), 36);
    }

    static String getDisplayName(GameKit kit, LanguageConfigurationFileLocale locale) {
        return Nirvana.getInstance().getLangFile().getString("KIT." + kit.getIdentifier().toUpperCase() + ".NAME", locale);
    }

}
