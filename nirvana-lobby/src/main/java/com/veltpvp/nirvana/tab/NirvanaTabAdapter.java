package com.veltpvp.nirvana.tab;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.ikari.azazel.tab.TabAdapter;
import us.ikari.azazel.tab.TabTemplate;

import java.util.LinkedHashMap;
import java.util.UUID;

public class NirvanaTabAdapter implements TabAdapter {

    private static Nirvana main = Nirvana.getInstance();

    @Override
    public TabTemplate getTemplate(Player player){
        TabTemplate template = new TabTemplate();

        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        if (profile != null) {
            LobbyProfile.GamePlayerDatabaseFragment fragment = profile.getFragment();

            template.farRight(4, ChatColor.RED + "" + ChatColor.BOLD + "Warning!");
            template.farRight(6, ChatColor.GREEN + "Please use");
            template.farRight(7, ChatColor.GREEN + "1.7 for the");
            template.farRight(8, ChatColor.GREEN + "optimal playing");
            template.farRight(9, ChatColor.GREEN + "experience.");

            template.left(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "VELT SKYWARS");
            template.middle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "VELT SKYWARS");
            template.right(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "VELT SKYWARS");

            template.left("");
            template.left(ChatColor.YELLOW + "" + ChatColor.BOLD + "You");
            template.left(ChatColor.GRAY + "Rank: " + ChatColor.WHITE + "Member");
            template.left(ChatColor.GRAY + "Total Kills: " + ChatColor.WHITE + fragment.getTotalKills());
            template.left(ChatColor.GRAY + "Total Deaths: " + ChatColor.WHITE + fragment.getTotalDeaths());
            template.left("");
            template.left(ChatColor.YELLOW + "" + ChatColor.BOLD + "Lobby");
            template.left(ChatColor.GRAY + "ID: " + ChatColor.WHITE + main.getId().toUpperCase());
            template.left(ChatColor.GRAY + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());

            template.right("");

            for (Gamemode gamemode : Gamemode.getGamemodes()) {
                NirvanaServerType type;
                try {
                    type = NirvanaServerType.valueOf(gamemode.getId());
                } catch (Exception e) {
                    type = NirvanaServerType.POTPVP;
                }

                template.right(ChatColor.YELLOW + "" + ChatColor.BOLD + gamemode.getName());
                template.right(ChatColor.GRAY + "Wins: " + ChatColor.WHITE + fragment.getWins().getOrDefault(type, 0));
                template.right(ChatColor.GRAY + "Losses: " + ChatColor.WHITE + fragment.getLosses().getOrDefault(type, 0));
                template.right("");
            }

            if (!(profile.getMembers().isEmpty())) {
                template.middle("");
                template.middle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Party");

                int i = 0;
                for (UUID uuid : profile.getMembers().keySet()) {
                    if (i == 0) {
                        template.middle(ChatColor.GRAY + "*" + ChatColor.BLUE + profile.getMembers().get(uuid));
                    } else {
                        template.middle(ChatColor.BLUE + profile.getMembers().get(uuid));
                    }
                    i++;
                }

            }

        }

        return template;
    }
}
