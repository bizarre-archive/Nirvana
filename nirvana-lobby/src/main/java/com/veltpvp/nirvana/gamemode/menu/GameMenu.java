package com.veltpvp.nirvana.gamemode.menu;

import com.google.common.collect.Maps;
import com.veltpvp.nirvana.gamemode.Gamemode;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class GameMenu extends Menu {

    public GameMenu() {
        super(ChatColor.LIGHT_PURPLE + "Game Selector");

        setPlaceholder(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int x = 1;
        for (Gamemode gamemode : Gamemode.getGamemodes()) {
            buttons.put(getSlot(x, 1), new GameButton(gamemode));
            x += 3;
        }

        return buttons;
    }

}