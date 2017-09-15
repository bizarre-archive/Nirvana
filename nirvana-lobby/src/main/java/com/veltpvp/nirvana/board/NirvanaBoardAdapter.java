package com.veltpvp.nirvana.board;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.ikari.phoenix.scoreboard.scoreboard.Board;
import us.ikari.phoenix.scoreboard.scoreboard.BoardAdapter;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardCooldown;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NirvanaBoardAdapter implements BoardAdapter {

    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

    @Override
    public String getTitle(Player player) {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "VeltPvP " + ChatColor.RESET + ChatColor.GRAY + "(SkyWars)";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {
        LobbyProfile profile = LobbyProfile.getByPlayer(player);
        List<String> toReturn = new ArrayList<>();

        if (profile != null) {

            toReturn.add(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------");

            if (profile.getQueue() == null) {
                toReturn.add(ChatColor.LIGHT_PURPLE + "In Lobby: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
                toReturn.add(ChatColor.LIGHT_PURPLE + "Server: " + ChatColor.WHITE + Nirvana.getInstance().getId().toUpperCase());
            } else {
                toReturn.add(ChatColor.LIGHT_PURPLE + "Queue Information");
                toReturn.add(ChatColor.GOLD + " * " + ChatColor.YELLOW + "Game Type " + ChatColor.GRAY + "- " + ChatColor.WHITE + profile.getQueue().getName());

                long time = System.currentTimeMillis() - profile.getQueue().getInit();
                String duration;

                if (time >= 60000) {
                    duration = DurationFormatUtils.formatDuration(System.currentTimeMillis() - profile.getQueue().getInit(), "mm:ss");
                } else {
                    duration = SECONDS_FORMATTER.format(time  / 1000.0f) + "s";
                }


                toReturn.add(ChatColor.GOLD + " * " + ChatColor.YELLOW + "Time Taken " + ChatColor.GRAY + "- " + ChatColor.WHITE + duration);
            }

            toReturn.add(" ");
            toReturn.add(ChatColor.LIGHT_PURPLE + "www.veltpvp.com");
            toReturn.add(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------");
        }

        return toReturn;
    }

}
