package com.veltpvp.nirvana.game.tab;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.Game;
import com.veltpvp.nirvana.game.GameState;
import com.veltpvp.nirvana.game.player.GamePlayer;
import com.veltpvp.nirvana.game.task.GameStartTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.ikari.azazel.tab.TabAdapter;
import us.ikari.azazel.tab.TabTemplate;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.ArrayList;
import java.util.List;

public class GameTabAdapter implements TabAdapter {

    private final Nirvana main;
    private final Game game;

    public GameTabAdapter(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @Override
    public TabTemplate getTemplate(Player player) {
        TabTemplate toReturn = new TabTemplate();

        toReturn.farRight(4, ChatColor.RED + "" + ChatColor.BOLD + "Warning!");
        toReturn.farRight(6, ChatColor.GREEN + "Please use");
        toReturn.farRight(7, ChatColor.GREEN + "1.7 for the");
        toReturn.farRight(8, ChatColor.GREEN + "optimal playing");
        toReturn.farRight(9, ChatColor.GREEN + "experience.");

        GamePlayer gamePlayer = game.getByPlayer(player);
        
        if (gamePlayer == null) {
            return null;
        }

        GameStartTask task = game.getTask(GameStartTask.class);
        if (game.getState() == GameState.LOBBY) {
            for (String line : main.getLangFile().getStringList("TAB." + (game.hasTask(GameStartTask.class) ? "LOBBY" : "LOBBY_WAITING") + ".LEFT", LanguageConfigurationFileLocale.ENGLISH, "N/A", gamePlayer != null ? gamePlayer.getKitName() : "N/A", game.getMap(), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), getCardinalDirection(player), getPlayers(), game.getAlivePlayers().size(), getSpectators(), Bukkit.getServerName(), game.getGameTime().secondsLeft(task != null ? task.getDuration() : main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1)) {
                toReturn.left(line);
            }
            for (String line : main.getLangFile().getStringList("TAB." + (game.hasTask(GameStartTask.class) ? "LOBBY" : "LOBBY_WAITING") + ".MIDDLE", LanguageConfigurationFileLocale.ENGLISH, "N/A", gamePlayer != null ? gamePlayer.getKitName() : "N/A", game.getMap(), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), getCardinalDirection(player), getPlayers(), game.getAlivePlayers().size(), getSpectators(), Bukkit.getServerName(), game.getGameTime().secondsLeft(task != null ? task.getDuration() : main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1)) {
                toReturn.middle(line);
            }
            for (String line : main.getLangFile().getStringList("TAB." + (game.hasTask(GameStartTask.class) ? "LOBBY" : "LOBBY_WAITING") + ".RIGHT", LanguageConfigurationFileLocale.ENGLISH, "N/A", gamePlayer != null ? gamePlayer.getKitName() : "N/A", game.getMap(), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), getCardinalDirection(player), getPlayers(), game.getAlivePlayers().size(), getSpectators(), Bukkit.getServerName(), game.getGameTime().secondsLeft(task != null ? task.getDuration() : main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1)) {
                toReturn.right(line);
            }
        }

        if (game.getState() == GameState.PLAY || game.getState() == GameState.END || game.getState() == GameState.DEATHMATCH) {
            for (String line : main.getLangFile().getStringList("TAB.GAME.LEFT", LanguageConfigurationFileLocale.ENGLISH, gamePlayer.getData().kills(), gamePlayer != null ? gamePlayer.getKitName() : "N/A", game.getMap(), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), getCardinalDirection(player), getPlayers(), game.getAlivePlayers().size(), getSpectators(), Bukkit.getServerName(), game.getGameTime().secondsLeft(task != null ? task.getDuration() : main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1)) {
                toReturn.left(line);
            }
            for (String line : main.getLangFile().getStringList("TAB.GAME.MIDDLE", LanguageConfigurationFileLocale.ENGLISH, gamePlayer.getData().kills(), gamePlayer != null ? gamePlayer.getKitName() : "N/A", game.getMap(), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), getCardinalDirection(player), getPlayers(), game.getAlivePlayers().size(), getSpectators(), Bukkit.getServerName(), game.getGameTime().secondsLeft(task != null ? task.getDuration() : main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1)) {
                toReturn.middle(line);
            }
            for (String line : main.getLangFile().getStringList("TAB.GAME.RIGHT", LanguageConfigurationFileLocale.ENGLISH, gamePlayer.getData().kills(), gamePlayer != null ? gamePlayer.getKitName() : "N/A", game.getMap(), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), getCardinalDirection(player), getPlayers(), game.getAlivePlayers().size(), getSpectators(), Bukkit.getServerName(), game.getGameTime().secondsLeft(task != null ? task.getDuration() : main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1)) {
                toReturn.right(line);
            }
        }

        return toReturn;
    }

    private List<String> getPlayers() {
        List<String> toReturn = new ArrayList<>();

        for (GamePlayer gamePlayer : game.getPlayers()) {
            if (gamePlayer.getData().alive()) {
                toReturn.add(main.getLangFile().getString("TAB.ALIVE", LanguageConfigurationFileLocale.ENGLISH) + gamePlayer.getName());
            } else {
                toReturn.add(main.getLangFile().getString("TAB.DEAD", LanguageConfigurationFileLocale.ENGLISH) + gamePlayer.getName());
            }
        }

        return toReturn;
    }


    private int getSpectators() {
        int toReturn = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = game.getByPlayer(player);
            if (gamePlayer != null) {
                if (gamePlayer.getData().spectator() != null) {
                    toReturn++;
                }
            }
        }

        return toReturn;
    }

    /*
        Stolen
     */
    private String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "W";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NW";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "N";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "NE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "E";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SE";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "S";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "SW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "SW";
        } else {
            return "N/A";
        }
    }
}
