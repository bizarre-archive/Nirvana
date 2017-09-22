package com.veltpvp.nirvana.parties.command;

import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.party.ErrorPacket;
import com.veltpvp.nirvana.packet.party.PartyLeavePacket;
import com.veltpvp.nirvana.packet.party.PartyMember;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.ikari.phoenix.command.Command;
import us.ikari.phoenix.command.CommandArgs;
import us.ikari.phoenix.network.packet.Packet;
import us.ikari.phoenix.network.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.rabbit.listener.RabbitNetworkDeliveryType;

public class PartyLeaveCommand extends BasePartyCommand {

    @Command(name = "party.leave", aliases = {"p.leave", "t.leave", "team.leave"})
    public void onCommand(CommandArgs command) {
        final Player player = command.getPlayer();
        main.getNetwork().sendPacket(new PartyLeavePacket(new PartyMember(player.getName(), player.getUniqueId())), NirvanaChannels.APPLICATION_CHANNEL, RabbitNetworkDeliveryType.DIRECT, new PacketResponseHandler() {
            public void onResponse(Packet packet) {
                if (packet instanceof ErrorPacket) {
                    player.sendMessage(ChatColor.RED + ((ErrorPacket) packet).getError());
                }
            }
        });
    }

}
