package com.veltpvp.nirvana.proxy;

import com.veltpvp.nirvana.packet.*;
import com.veltpvp.nirvana.packet.lobby.LobbyServerStatusPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import us.ikari.phoenix.network.packet.Packet;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.packet.event.PacketListener;
import us.ikari.phoenix.network.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.packet.handler.PacketExceptionHandler;
import us.ikari.phoenix.network.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.rabbit.listener.RabbitNetworkDeliveryType;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
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
                network = new RedisNetwork(new RedisNetworkConfiguration("10.0.9.2"), Nirvana.class.getClassLoader());
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
        network.sendPacket(new PlayerDisconnectPacket(player.getName(), player.getUniqueId()), NirvanaChannels.APPLICATION_CHANNEL, RabbitNetworkDeliveryType.DIRECT);
        for (List<String> players : playerQueue.keySet()) {
            System.out.println(players.toString());
            if (players.contains(player.getName())) {
                System.out.println("player contains, sending packet");
                network.sendPacket(new ServerRemoveQueuePlayer(player.getName()), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerSwitchServerConnectEvent(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        network.sendPacket(new PlayerSwitchServerPacket(player.getName(), player.getUniqueId(), event.getTarget().getName()), NirvanaChannels.APPLICATION_CHANNEL, RabbitNetworkDeliveryType.DIRECT);
    }

    @EventHandler //TODO: Cancel thread on application's end
    public void onPlayerSwitchServerEvent(ServerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        System.out.println(player.getName() + " IS SWITCHING SERVERS");
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

    @PacketListener({BootyCallPacket.class})
    public void onBootyCallPacketReceiveEvent(PacketReceiveEvent event) {
        System.out.println("Received booty call packet..");

        network.getResponses().clear();
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

            if (command.equalsIgnoreCase("getNirvanaCount")) {
                ServerInfo server = ProxyServer.getInstance().getPlayer(event.getReceiver().toString()).getServer().getInfo();
                network.sendPacket(new RequestPlayerCountPacket(), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT, 5000, new PacketResponseHandler() {
                    @Override
                    public void onResponse(Packet packet) {
                        if (packet instanceof ServerInfoPacket) {
                            ServerInfoPacket serverInfoPacket = (ServerInfoPacket) packet;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(stream);
                            try {
                                out.writeUTF("player-count");
                                out.writeInt(serverInfoPacket.getServer().getPlayers());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            server.sendData("Nirvana", stream.toByteArray());
                        }
                    }
                }, new PacketExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(stream);
                        try {
                            out.writeUTF("player-count");
                            out.writeInt(-1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        server.sendData("Nirvana", stream.toByteArray());
                    }
                });
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

                System.out.println("Sending packet..");

                network.sendPacket(new ServerSelectPacket(NirvanaServerType.LOBBY, players), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT, 10000, new PacketResponseHandler() {
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
