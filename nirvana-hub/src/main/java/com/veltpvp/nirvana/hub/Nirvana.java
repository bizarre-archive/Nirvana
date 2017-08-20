package com.veltpvp.nirvana.hub;

import com.veltpvp.nirvana.packet.*;
import com.veltpvp.nirvana.packet.lobby.LobbyServer;
import com.veltpvp.nirvana.packet.lobby.LobbyServerListPacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServerRemovePacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServerStatusPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.packet.Packet;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.redis.packet.event.PacketListener;
import us.ikari.phoenix.network.redis.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.packet.handler.PacketExceptionHandler;
import us.ikari.phoenix.network.redis.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.redis.thread.RedisNetworkListThread;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Nirvana {

    private static final int MINIMUM_SKYWARS_ALLOCATION_COUNT = 1;

    private final RedisNetwork network;
    private final List<NirvanaServer> servers;
    private final Set<LobbyServer> lobbies;
    private final ExecutorService pool;
    private final List<QueuePacketFragment> queue;

    public Nirvana(final RedisNetwork network) {
        System.out.println("Nirvana application server launched.");

        this.network = network;
        this.pool = Executors.newCachedThreadPool();
        this.network.registerPacketListener(this);
        this.servers = new ArrayList<>();
        this.lobbies = new HashSet<>();
        this.queue = Collections.synchronizedList(new ArrayList<QueuePacketFragment>());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                network.shutdown();
            }
        });

        network.registerThread(new RedisNetworkListThread(network, NirvanaChannels.APPLICATION_CHANNEL));
        System.out.println("Sending booty call to all slaves.");
        network.sendPacket(new BootyCallPacket(), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    network.sendPacket(new LobbyServerListPacket(new ArrayList<>(lobbies)), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (true) {

                    outer: for (final NirvanaServerType type : NirvanaServerType.values()) {
                        if (type == NirvanaServerType.LOBBY) continue; //TODO: Replacement for lobbies
                        if (type == NirvanaServerType.PENDING) continue;
                        final List<QueuePacketFragment> queues = QueuePacketFragment.getAllByType(type, queue);

                        if (QueuePacketFragment.getInQueue(type, queue) >= MINIMUM_SKYWARS_ALLOCATION_COUNT) {
                            for (final NirvanaServer server : servers) {
                                if (server.getType() == NirvanaServerType.PENDING) {
                                    System.out.println(type + " SENDING PACKETS");
                                    server.setType(type);
                                    network.sendSyncPacket(new ServerSetTypePacket(server.getId(), type), server.getId(), PacketDeliveryMethod.DIRECT, Integer.MAX_VALUE, new PacketResponseHandler() {
                                        @Override
                                        public void onResponse(Packet packet) {

                                            for (QueuePacketFragment fragment : queues) {
                                                fragment.getEvent().setResponse(new ServerInfoPacket(server));
                                            }

                                            queue.removeAll(queues);
                                        }
                                    }, new PacketExceptionHandler() {
                                        @Override
                                        public void onException(Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    break outer;
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @PacketListener({ServerInfoPacket.class})
    public void onServerStatusPacketReceiveEvent(PacketReceiveEvent event) {
        ServerInfoPacket packet = (ServerInfoPacket) event.getPacket();
        NirvanaServer server = packet.getServer();

        if (event.getChannel().equalsIgnoreCase(NirvanaChannels.APPLICATION_CHANNEL)) {
            NirvanaServer local = getById(server.getId());
            if (local == null) {
                System.out.println("Server added. (ID: " + server.getId() + ", TYPE: " + server.getType() + ", STATUS: " + server.getStatus().name() + ", PLAYERS: " + packet.getServer().getPlayers() + ", MAX: " + packet.getServer().getMaxPlayers() + ")");

                servers.add(packet.getServer());
            } else {
                local.setStatus(server.getStatus());
                local.setType(server.getType());
                System.out.println("Server updated. (ID: " + server.getId() + ", TYPE: " + server.getType() + ", STATUS: " + server.getStatus().name() + ", PLAYERS: " + packet.getServer().getPlayers() + ", MAX: " + packet.getServer().getMaxPlayers() + ")");
            }
        }
    }

    @PacketListener({LobbyServerRemovePacket.class})
    public void onLobbyServerRemovePacketReceiveEvent(PacketReceiveEvent event) {
        LobbyServerRemovePacket packet = (LobbyServerRemovePacket) event.getPacket();
        LobbyServer server = getLobbyById(packet.getId());

        if (server != null) {
            System.out.println("Server removed. (ID: " + server.getId() + ", TYPE: LOBBY)");

            lobbies.remove(server);

            network.sendPacket(new LobbyServerListPacket(new ArrayList<>(lobbies)), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
        }

    }

    @PacketListener({LobbyServerStatusPacket.class})
    public void onLobbyServerStatusPacketReceiveEvent(PacketReceiveEvent event) {
        LobbyServerStatusPacket packet = (LobbyServerStatusPacket) event.getPacket();
        LobbyServer server = packet.getServer();

        LobbyServer copy = getLobbyById(server.getId());
        if (copy == null) {
            System.out.println("Server added. (ID: " + server.getId() + ", ONLINE: " + server.getPlayers() + ", MAX: " + server.getMaxPlayers() + ", TYPE: LOBBY)");
            lobbies.add(server);

            network.sendPacket(new LobbyServerListPacket(new ArrayList<>(lobbies)), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
        } else {
            copy.setMaxPlayers(server.getMaxPlayers());
            copy.setPlayers(server.getPlayers());
            copy.setUpdatedAt(server.getUpdatedAt());

            System.out.println("Server updated. (ID: " + server.getId() + ", ONLINE: " + server.getPlayers() + ", MAX: " + server.getMaxPlayers() + ", TYPE: LOBBY)");
        }

    }

    @PacketListener({ServerRemoveQueuePlayer.class})
    public void onServerRemoveQueuePlayerPacketReceiveEvent(PacketReceiveEvent event) {
        ServerRemoveQueuePlayer packet = (ServerRemoveQueuePlayer) event.getPacket();

        QueuePacketFragment fragment = QueuePacketFragment.getByPlayer(packet.getPlayer(), queue);

        if (fragment != null) {
            System.out.println("removed " + packet.getPlayer() + " from queue");
            fragment.getPlayers().remove(packet.getPlayer());

            if (fragment.getPlayers().isEmpty()) {
                queue.remove(fragment);
            }

        }
    }

    @PacketListener({ServerSelectPacket.class})
    public void onServerSelectPacketReceiveEvent(final PacketReceiveEvent event) {
        final NirvanaServerType type = ((ServerSelectPacket)event.getPacket()).getType();
        final int count = ((ServerSelectPacket) event.getPacket()).getPlayers().size();

        System.out.println("Packet received..");

        if (type == NirvanaServerType.PENDING) {
            System.out.println("Cannot return pending server");
            return;
        }

        if (type == NirvanaServerType.LOBBY) {
            for (LobbyServer server : lobbies) {
                if (server.getPlayers() + count <= server.getMaxPlayers()) {
                    event.setResponse(new LobbyServerStatusPacket(server));
                    return;
                }
            }
        } else {
            for (NirvanaServer server : servers) {
                System.out.println(server.getType() + ":" + type);
                if (server.getType() == type && server.getPlayers() + count <= server.getMaxPlayers() && (server.getStatus() == NirvanaServerStatus.WAITING_FOR_PLAYERS || server.getStatus() == NirvanaServerStatus.STARTING)) {
                    event.setAsyncResponse(true);

                    network.sendPacket(new BootyCallPacket(), server.getId(), PacketDeliveryMethod.DIRECT, new PacketResponseHandler() {
                        @Override
                        public void onResponse(Packet packet) {
                            if (packet instanceof ServerInfoPacket) {
                                ServerInfoPacket serverInfoPacket = (ServerInfoPacket) packet;
                                NirvanaServer check = serverInfoPacket.getServer();

                                if (check.getType() == type && check.getPlayers() + count <= check.getMaxPlayers() && (check.getStatus() == NirvanaServerStatus.WAITING_FOR_PLAYERS || check.getStatus() == NirvanaServerStatus.STARTING)) {
                                    event.setResponse(new ServerInfoPacket(check));
                                    return;
                                }

                            }

                            queue.add(new QueuePacketFragment(type, event, ((ServerSelectPacket) event.getPacket()).getPlayers()));
                        }
                    }, new PacketExceptionHandler() {
                        @Override
                        public void onException(Exception e) {
                            queue.add(new QueuePacketFragment(type, event, ((ServerSelectPacket) event.getPacket()).getPlayers()));
                        }
                    });
                    return;
                }
            }
        }

        event.setAsyncResponse(true);
        queue.add(new QueuePacketFragment(type, event, ((ServerSelectPacket) event.getPacket()).getPlayers()));

        /*this.pool.execute(new Runnable() { // Our own makeshift queue
            @Override
            public void run() {
                if (type == NirvanaServerType.LOBBY) {

                    LobbyServer selectedLobby = null;
                    while (selectedLobby == null) {
                        for (LobbyServer server : lobbies) {
                            if (server.getPlayers() < server.getMaxPlayers()) {
                                System.out.println("Found lobby: " + server.getId());
                                selectedLobby = server;
                            }
                        }

                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("Replied..");
                    event.setResponse(new LobbyServerStatusPacket(selectedLobby));
                } else {
                    NirvanaServer selectedServer = null;
                    while (selectedServer == null) {
                        for (NirvanaServer server : servers) {
                            if (server.getType().equals(type) && server.getStatus() == NirvanaServerStatus.WAITING_FOR_PLAYERS && server.getPlayers() +  count <= server.getMaxPlayers()) {
                                System.out.println("Found server " + server.getId());
                                selectedServer = server;
                                break;
                            }
                        }

                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("Replied..");
                    event.setResponse(new ServerInfoPacket(selectedServer));
                    System.out.println(" ");
                }
            }
        });*/

    }

    public LobbyServer getLobbyById(String id) {
        for (LobbyServer server : lobbies) {
            if (server.getId().equalsIgnoreCase(id)) {
                return server;
            }
        }
        return null;
    }


    public NirvanaServer getById(String id) {
        for (NirvanaServer server : servers) {
            if (server.getId().equalsIgnoreCase(id)) {
                return server;
            }
        }
        return null;
    }

    private static class QueuePacketFragment {
        @Getter private final NirvanaServerType type;
        @Getter private final PacketReceiveEvent event;
        @Getter private final List<String> players;

        private QueuePacketFragment(NirvanaServerType type, PacketReceiveEvent event, List<String> players) {
            this.type = type;
            this.event = event;
            this.players = players;
        }

        public static QueuePacketFragment getByPlayer(String player, List<QueuePacketFragment> list) {
            for (QueuePacketFragment fragment : list) {
                if (fragment.getPlayers().contains(player)) {
                    return fragment;
                }
            }
            return null;
        }

        public static int getInQueue(NirvanaServerType type, List<QueuePacketFragment> list) {
            int toReturn = 0;

            for (QueuePacketFragment fragment : list) {
                if (type == fragment.getType()) {
                    toReturn += fragment.getPlayers().size();
                }
            }

            return toReturn;
        }

        public static List<QueuePacketFragment> getAllByType(NirvanaServerType type, List<QueuePacketFragment> list) {
            List<QueuePacketFragment> toReturn = Collections.synchronizedList(new ArrayList<QueuePacketFragment>());

            for (QueuePacketFragment fragment : list) {
                if (fragment.getType() == type) {
                    toReturn.add(fragment);
                }
            }

            return toReturn;
        }
    }

    public static void main(String[] args) {
        new Nirvana(new RedisNetwork(new RedisNetworkConfiguration("localhost")));
    }

}
