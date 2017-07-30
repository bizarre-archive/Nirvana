package com.veltpvp.nirvana.proxy;

import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerSelectPacket;
import com.veltpvp.nirvana.packet.ServerStatusPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.packet.Packet;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.redis.packet.handler.PacketExceptionHandler;
import us.ikari.phoenix.network.redis.packet.handler.PacketResponseHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Nirvana extends Plugin implements Listener {

    private RedisNetwork network;
    private Map<String, Integer> playerQueue;

    @Override
    public void onEnable() {
        network = new RedisNetwork(new RedisNetworkConfiguration("localhost"));
        playerQueue = new HashMap<>();

        ProxyServer.getInstance().registerChannel("Nirvana");
    }

    @EventHandler //TODO: Cancel thread on application's end
    public void onPlayerQuitEvent(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (playerQueue.containsKey(player.getName())) {
            ProxyServer.getInstance().getScheduler().cancel(playerQueue.get(player.getName()));
            playerQueue.remove(player.getName());
        }
    }

    @EventHandler //TODO: Cancel thread on application's end
    public void onPlayerSwitchServerEvent(ServerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (playerQueue.containsKey(player.getName())) {
            ProxyServer.getInstance().getScheduler().cancel(playerQueue.get(player.getName()));
            playerQueue.remove(player.getName());
        }
    }

    @EventHandler //TODO: Clean this the fuck up, ew
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

            String command;
            try {
                command = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (command.equalsIgnoreCase("getNirvanaServer")) {

                String type;
                try {
                    type = in.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                String player;
                try {
                    player = in.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);

                if (proxiedPlayer != null) {
                    playerQueue.put(player, ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {
                        @Override
                        public void run() {
                            network.sendPacket(new ServerSelectPacket(type), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT, Integer.MAX_VALUE, new PacketResponseHandler() {
                                @Override
                                public void onResponse(Packet packet) {
                                    if (packet instanceof ServerStatusPacket) {
                                        ServerStatusPacket serverPacket = (ServerStatusPacket) packet;

                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        DataOutputStream out = new DataOutputStream(stream);

                                        try {
                                            out.writeUTF(command);
                                            out.writeUTF(player);
                                            out.writeUTF(serverPacket.getServer().getId());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            return;
                                        }

                                        proxiedPlayer.getServer().sendData("Nirvana", stream.toByteArray());
                                        playerQueue.remove(player);
                                    }
                                }
                            }, new PacketExceptionHandler() {
                                @Override
                                public void onException(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }).getId());
                }
            }

        }
    }

}
