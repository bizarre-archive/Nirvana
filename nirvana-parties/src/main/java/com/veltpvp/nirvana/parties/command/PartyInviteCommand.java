package com.veltpvp.nirvana.parties.command;

import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.party.ErrorPacket;
import com.veltpvp.nirvana.packet.party.PartyInvitePacket;
import com.veltpvp.nirvana.packet.party.PartyMember;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import us.ikari.phoenix.command.Command;
import us.ikari.phoenix.command.CommandArgs;
import us.ikari.phoenix.network.packet.Packet;
import us.ikari.phoenix.network.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.rabbit.listener.RabbitNetworkDeliveryType;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

public class PartyInviteCommand extends BasePartyCommand {

    @Command(name = "party.invite", aliases = {"p.invite", "t.invite", "team.invite"})
    public void onCommand(CommandArgs command) {
        final Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Missing player argument");
            return;
        }

        String toInvite = args[0];

        if (toInvite.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't invite yourself.");
            return;
        }

        Map.Entry<PartyMember, String> entry = qLib.getInstance().runBackboneRedisCommand(new RedisCommand<Map.Entry<PartyMember, String>>() {
            @Override
            public Map.Entry<PartyMember, String> execute(Jedis jedis) {
                UUID uuid = FrozenUUIDCache.uuid(toInvite);

                if (uuid != null) {
                    String name = FrozenUUIDCache.name(uuid);

                    if (jedis.exists("player:" + name.toLowerCase())) {
                        String server = jedis.get("player:" + name.toLowerCase());

                        server = server.substring(server.indexOf(':') + 1);

                        return new AbstractMap.SimpleEntry<PartyMember, String>(new PartyMember(name, uuid), server);
                    }
                }

                return null;
            }
        });

        if (entry == null) {
            player.sendMessage(ChatColor.RED + "Unable to find player.");
            return;
        }

        main.getNetwork().sendPacket(new PartyInvitePacket(new PartyMember(player.getName(), player.getUniqueId()), entry.getKey(), entry.getValue()), NirvanaChannels.APPLICATION_CHANNEL, RabbitNetworkDeliveryType.DIRECT, new PacketResponseHandler() {
            public void onResponse(Packet packet) {
                if (packet instanceof ErrorPacket) {
                    player.sendMessage(ChatColor.RED + ((ErrorPacket) packet).getError());
                }
            }
        });
    }

}
