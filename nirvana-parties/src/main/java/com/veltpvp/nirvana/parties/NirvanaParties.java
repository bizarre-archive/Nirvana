package com.veltpvp.nirvana.parties;

import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.party.PartyMember;
import com.veltpvp.nirvana.packet.party.PartyUpdatePacket;
import com.veltpvp.nirvana.parties.command.*;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.ikari.phoenix.command.CommandFramework;
import us.ikari.phoenix.network.packet.event.PacketListener;
import us.ikari.phoenix.network.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.thread.RedisNetworkSubscribeThread;

public class NirvanaParties extends JavaPlugin {

    private static NirvanaParties instance;
    @Getter private CommandFramework framework;
    @Getter private RedisNetwork network;

    @Override
    public void onEnable() {
        instance = this;

        network = new RedisNetwork(new RedisNetworkConfiguration("142.44.138.178"));
        network.registerThread(new RedisNetworkSubscribeThread(network, NirvanaChannels.SLAVE_CHANNEL));

        network.registerPacketListener(this);
        framework = new CommandFramework(this);

        registerCommands();
    }

    @PacketListener({PartyUpdatePacket.class})
    public void onPartyUpdatePacketReceiveEvent(PacketReceiveEvent event) {
        PartyUpdatePacket packet = (PartyUpdatePacket) event.getPacket();

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.DISBAND) {
            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Your party has been disbanded by " + ((PartyUpdatePacket) event.getPacket()).getOptional().getName() + "!");
                }

            }
        }

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.INVITE_PLAYER) {
            Player invited = Bukkit.getPlayer(packet.getOptional().getUuid());

            if (invited != null) {
                TextComponent component = new TextComponent(ChatColor.YELLOW + "You have been invited to join " + ChatColor.GREEN + packet.getParty().getMembers().get(0).getName() + ChatColor.YELLOW + "'s party!");
                TextComponent extra = new TextComponent(ChatColor.GRAY + " [Click here to join]");
                extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click here to join " + ChatColor.LIGHT_PURPLE + packet.getParty().getMembers().get(0).getName() + "'s party.").create()));
                extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + packet.getParty().getMembers().get(0).getName()));

                component.addExtra(extra);

                invited.spigot().sendMessage(component);
            }

            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    player.sendMessage(ChatColor.GREEN + packet.getOptional().getName() + ChatColor.YELLOW + " has been invited to the party!");
                }

            }
        }

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.ADD_PLAYER) {
            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    player.sendMessage(ChatColor.GREEN + packet.getOptional().getName() + ChatColor.YELLOW + " has joined the party!");
                }

            }
        }

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_LEAVE) {
            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    player.sendMessage(ChatColor.RED + packet.getOptional().getName() + " has left the party!");
                }

            }
        }

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_KICK) {
            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    player.sendMessage(ChatColor.RED + packet.getOptional().getName() + " was kicked from the party!");
                }

            }
        }


    }

    @Override
    public void onDisable() {
        network.close();
    }

    private void registerCommands() {
        new PartyCommand();
        new PartyCreateCommand();
        new PartyDisbandCommand();
        new PartyInviteCommand();
        new PartyJoinCommand();
        new PartyKickCommand();
        new PartyLeaveCommand();
    }

    public static NirvanaParties getInstance() {
        return instance;
    }
}
