package com.veltpvp.nirvana.parties.command;

import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.party.ErrorPacket;
import com.veltpvp.nirvana.packet.party.PartyCreatePacket;
import com.veltpvp.nirvana.packet.party.PartyMember;
import com.veltpvp.nirvana.parties.event.PartyCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.ikari.phoenix.command.Command;
import us.ikari.phoenix.command.CommandArgs;
import us.ikari.phoenix.network.packet.Packet;
import us.ikari.phoenix.network.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.rabbit.listener.RabbitNetworkDeliveryType;

public class PartyCreateCommand extends BasePartyCommand {

    @Command(name = "party.create", aliases = {"p.create", "t.create", "team.create"})
    public void onCommand(CommandArgs command) {
        final Player player = command.getPlayer();
        main.getNetwork().sendPacket(new PartyCreatePacket(new PartyMember(player.getName(), player.getUniqueId())), NirvanaChannels.APPLICATION_CHANNEL, RabbitNetworkDeliveryType.DIRECT, new PacketResponseHandler() {
            public void onResponse(Packet packet) {
                if (packet instanceof ErrorPacket) {
                    player.sendMessage(ChatColor.RED + ((ErrorPacket) packet).getError());
                } else {
                    Bukkit.getPluginManager().callEvent(new PartyCreateEvent(player));
                }
            }
        });
    }

}
