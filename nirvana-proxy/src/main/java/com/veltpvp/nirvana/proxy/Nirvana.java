package com.veltpvp.nirvana.proxy;

import com.veltpvp.nirvana.packet.*;
import com.veltpvp.nirvana.packet.lobby.LobbyServerStatusPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.packet.Packet;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.redis.packet.event.PacketListener;
import us.ikari.phoenix.network.redis.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.packet.handler.PacketExceptionHandler;
import us.ikari.phoenix.network.redis.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.redis.thread.RedisNetworkSubscribeThread;

import java.io.*;
import java.util.*;

public class Nirvana extends Plugin implements Listener {

    private static Nirvana instance;

    @Getter private RedisNetwork network;
    private Map<List<String>, Integer> playerQueue;

    @Override
    public void onEnable() {
        instance = this;

        playerQueue = new HashMap<>();

        ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                network = new RedisNetwork(new RedisNetworkConfiguration("localhost"), Nirvana.class.getClassLoader());
                network.registerThread(new RedisNetworkSubscribeThread(network, NirvanaChannels.SLAVE_CHANNEL));
                network.registerPacketListener(Nirvana.this);
            }
        });

        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        ProxyServer.getInstance().registerChannel("Nirvana");
    }

    @Override
    public void onDisable() {
        network.shutdown();
    }

    @EventHandler //TODO: Cancel thread on application's end
    public void onPlayerQuitEvent(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        System.out.println(player.getName() + " IS DISCONNECTING");
        System.out.println(playerQueue.keySet().toString());
        for (List<String> players : playerQueue.keySet()) {
            System.out.println(players.toString());
            if (players.contains(player.getName())) {
                System.out.println("player contains, sending packet");
                network.sendPacket(new ServerRemoveQueuePlayer(player.getName()), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);
                break;
            }
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

    @PacketListener({ServerSendPlayerPacket.class})
    public void onServerSendPlayerPacketReceiveEvent(PacketReceiveEvent event) {
        ServerSendPlayerPacket packet = (ServerSendPlayerPacket) event.getPacket();

        System.out.println("send server packet");

        for (String player : packet.getPlayers()) {
            System.out.println(player);
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);

            if (proxiedPlayer != null) {
                System.out.println("sending " + player);
                proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(packet.getServer()));
            }

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

            if (command.equalsIgnoreCase("sendToNirvanaLobby")) {
                System.out.println("Got send to server game msg");

                List<String> players = new ArrayList<>();
                try {
                    int count = in.readInt();
                    for (int i = 0; i < count; i++) {
                        players.add(in.readUTF());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                System.out.println("players = " + players.toString());

                Iterator<String> iterator = players.iterator();
                while (iterator.hasNext()) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(iterator.next());
                    if (player == null) {
                        iterator.remove();
                    }
                }

                System.out.println("Sending packet..");

                playerQueue.put(players, ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {
                    @Override
                    public void run() {
                        network.sendPacket(new ServerSelectPacket(NirvanaServerType.LOBBY, players), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT, Integer.MAX_VALUE, new PacketResponseHandler() {
                            @Override
                            public void onResponse(Packet packet) {
                                System.out.println("Received packet");
                                if (packet instanceof LobbyServerStatusPacket) {
                                    System.out.println("is instance");
                                    LobbyServerStatusPacket serverPacket = (LobbyServerStatusPacket) packet;

                                    System.out.println("iterating over players");

                                    for (String name : players) {
                                        System.out.println("foundm player " + name);
                                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
                                        if (player != null) {
                                            System.out.println("player not null");
                                            player.connect(ProxyServer.getInstance().getServerInfo(serverPacket.getServer().getId()));
                                            System.out.println("sent player");
                                        }
                                    }

                                    playerQueue.remove(players);
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

            if (command.equalsIgnoreCase("sendToNirvanaGame")) {

                System.out.println("Got send to server game msg");

                NirvanaServerType type;
                try {
                    type = NirvanaServerType.valueOf(in.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                System.out.println("Type = " + type);

                List<String> players = new ArrayList<>();
                try {
                    int count = in.readInt();
                    for (int i = 0; i < count; i++) {
                        players.add(in.readUTF());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                System.out.println("players = " + players.toString());

                Iterator<String> iterator = players.iterator();
                while (iterator.hasNext()) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(iterator.next());
                    if (player == null) {
                        iterator.remove();
                    }
                }

                System.out.println("Sending packet..");

                playerQueue.put(players, 0);

                network.sendPacket(new ServerSelectPacket(type, players), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT, Integer.MAX_VALUE, new PacketResponseHandler() {
                    @Override
                    public void onResponse(Packet packet) {
                        System.out.println("Received packet");
                        if (packet instanceof ServerInfoPacket) {
                            System.out.println("is instance");
                            ServerInfoPacket serverPacket = (ServerInfoPacket) packet;

                            System.out.println(serverPacket.getServer().getId());

                            network.sendPacket(new ServerSendPlayerPacket(serverPacket.getServer().getId(), players), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);

                            playerQueue.remove(players);
                        }
                    }
                }, new PacketExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }

    public static Nirvana getInstance() {
        return instance;
    }
}
