package com.veltpvp.nirvana.hub;

import com.veltpvp.nirvana.packet.*;
import com.veltpvp.nirvana.packet.lobby.LobbyServer;
import com.veltpvp.nirvana.packet.lobby.LobbyServerListPacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServerRemovePacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServerStatusPacket;
import com.veltpvp.nirvana.packet.party.*;
import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.packet.event.PacketListener;
import us.ikari.phoenix.network.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.packet.handler.PacketExceptionHandler;
import us.ikari.phoenix.network.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.rabbit.listener.RabbitNetworkDeliveryType;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.thread.RedisNetworkListThread;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Nirvana {

    private static final int MINIMUM_SKYWARS_ALLOCATION_COUNT = 1;

    private final RedisNetwork network;
    private final List<NirvanaServer> servers;
    private final List<LobbyServer> lobbies;
    private final ExecutorService pool;
    private final List<QueuePacketFragment> queue;
    private final Map<PartyMember, Party> partyMemberMap;
    private final Map<Party, UUID> partyToUuidMap;
    private final Map<UUID, Party> parties;

    public Nirvana(final RedisNetwork network) {
        System.out.println("Nirvana application server launched.");

        this.network = network;
        this.pool = Executors.newCachedThreadPool();
        this.network.registerPacketListener(this);
        this.servers = new ArrayList<>();
        this.parties = new ConcurrentHashMap<>();
        this.partyMemberMap = new ConcurrentHashMap<>();
        this.partyToUuidMap = new ConcurrentHashMap<>();
        this.lobbies = Collections.synchronizedList(new ArrayList<LobbyServer>());
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
                    for (final NirvanaServer server : servers) {
                        if (server.getType() != NirvanaServerType.PENDING && server.getPlayers() == 0) {
                            network.sendPacket(new ServerSetTypePacket(server.getId(), NirvanaServerType.PENDING), server.getId(), PacketDeliveryMethod.DIRECT);
                            server.setType(NirvanaServerType.PENDING);
                        }
                    }
                    try {
                        Thread.sleep(1000);
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

                    for (final NirvanaServerType type : NirvanaServerType.values()) {
                        if (type == NirvanaServerType.LOBBY) continue; //TODO: Replacement for lobbies
                        if (type == NirvanaServerType.PENDING) continue;
                        final List<QueuePacketFragment> queues = QueuePacketFragment.getAllByType(type, queue);

                        if (QueuePacketFragment.getInQueue(type, queue) >= MINIMUM_SKYWARS_ALLOCATION_COUNT) {
                            for (final NirvanaServer server : servers) {
                                if (server.getType() == NirvanaServerType.PENDING) {
                                    System.out.println(type + " sending packet to " + server.getId());
                                    server.setType(type);

                                    network.sendPacket(new ServerSetTypePacket(server.getId(), type), server.getId(), PacketDeliveryMethod.DIRECT, 5000, new PacketResponseHandler() {
                                        @Override
                                        public void onResponse(Packet packet) {
                                            for (QueuePacketFragment fragment : queues) {
                                                System.out.println(fragment.getPlayers() + " PLAYERS");
                                                fragment.getEvent().setResponse(new ServerInfoPacket(server));
                                            }
                                        }
                                    }, new PacketExceptionHandler() {
                                        @Override
                                        public void onException(Exception e) {
                                            System.out.println("Couldn't find a game, re-queueing for " + type.name());
                                            queue.addAll(queues);
                                        }
                                    });

                                    queue.removeAll(queues);
                                    break;
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @PacketListener({PartyLeavePacket.class})
    public void onPartyLeavePacketReceiveEvent(PacketReceiveEvent event) {
        PartyLeavePacket packet = (PartyLeavePacket) event.getPacket();
        PartyMember member = packet.getMember();

        Party party = partyMemberMap.get(member);
        if (party == null) {
            event.setResponse(new ErrorPacket("You're not in a party!"));
            return;
        }

        party.getMembers().remove(member);
        partyMemberMap.remove(member);

        event.setResponse(new ErrorPacket("You've successfully left the party."));
        network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_LEAVE, member), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
    }

    @PacketListener({PartyKickPacket.class})
    public void onPartyKickPacketReceiveEvent(PacketReceiveEvent event) {
        PartyKickPacket packet = (PartyKickPacket) event.getPacket();

        Party party = partyMemberMap.get(packet.getLeader());
        if (party == null) {
            event.setResponse(new ErrorPacket("You're not in a party!"));
            return;
        }

        if (!(party.getMembers().contains(packet.getToKick()))) {
            event.setResponse(new ErrorPacket(packet.getToKick().getName() + " isn't in your party!"));
            return;
        }

        party.getMembers().remove(packet.getToKick());
        partyMemberMap.remove(packet.getToKick());

        network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_KICK, packet.getToKick()), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
    }

    @PacketListener({PartyCreatePacket.class})
    public void onPartyCreatePacketReceiveEvent(PacketReceiveEvent event) {
        PartyCreatePacket packet = (PartyCreatePacket) event.getPacket();
        PartyMember member = packet.getLeader();

        System.out.println("GOT PARTY CREATE PACKET");
        Party party = partyMemberMap.get(member);
        System.out.println(partyMemberMap.size());
        if (party != null) {
            System.out.println("PARTY ALREADY CREATED");
            event.setResponse(new ErrorPacket("You're already in a party!"));
        } else {
            UUID uuid = UUID.randomUUID();
            System.out.println("PARTY NONEXISTENT CREATING NEW");

            party = new Party(uuid);
            party.getMembers().add(member);

            parties.put(uuid, party);
            partyMemberMap.put(member, party);
            partyToUuidMap.put(party, uuid);

            event.setResponse(new PartyInfoPacket(party));
        }
    }

    @PacketListener({PartyDisbandPacket.class})
    public void onPartyUpdatePacketReceiveEvent(PacketReceiveEvent event) {
        PartyDisbandPacket packet = (PartyDisbandPacket) event.getPacket();

        Party party = partyMemberMap.get(packet.getLeader());
        if (party == null) {
            event.setResponse(new ErrorPacket("You're not in a party!"));
            return;
        }

        if (!party.isLeader(packet.getLeader())) {
            event.setResponse(new ErrorPacket("You must be the leader to disband the party!"));
            return;
        }

        for (PartyMember member : party.getMembers()) {
            partyMemberMap.remove(member);
        }

        parties.remove(party.getUuid());
        partyToUuidMap.remove(party);

        event.setResponse(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.DISBAND, packet.getLeader()));
        network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.DISBAND, packet.getLeader()), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
    }

    @PacketListener({PartyJoinPacket.class})
    public void onPartyJoinPacketReceiveEvent(PacketReceiveEvent event) {
        PartyJoinPacket packet = (PartyJoinPacket) event.getPacket();

        Party party = partyMemberMap.get(packet.getMember());
        if (party != null) {
            event.setResponse(new ErrorPacket("You're already in a party!"));
            return;
        }

        party = partyMemberMap.get(packet.getToJoin());
        if (party == null) {
            event.setResponse(new ErrorPacket(packet.getToJoin().getName() + " isn't in a party!"));
            return;
        }

        if (!party.getInvitedPlayers().contains(packet.getMember())) {
            event.setResponse(new ErrorPacket("You haven't been invited to that party!"));
            return;
        }

        event.setResponse(new PartyInfoPacket(party));
        party.getInvitedPlayers().remove(packet.getMember());
        party.getMembers().add(packet.getMember());
        partyMemberMap.put(packet.getMember(), party);
        network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.ADD_PLAYER, packet.getMember()), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
    }

    @PacketListener({GetPartyPacket.class})
    public void onGetPartyPacketReceiveEvent(PacketReceiveEvent event) {
        GetPartyPacket packet = (GetPartyPacket) event.getPacket();

        Party party = partyMemberMap.get(packet.getLeader());
        if (party == null) {
            event.setResponse(new ErrorPacket("NOT IN PARTY"));
            return;
        }

        event.setResponse(new PartyInfoPacket(party));
    }

    @PacketListener({PartyInvitePacket.class})
    public void onPartyInvitePacketReceiveEvent(PacketReceiveEvent event) {
        PartyInvitePacket packet = (PartyInvitePacket) event.getPacket();

        Party party = partyMemberMap.get(packet.getLeader());
        if (party == null) {
            event.setResponse(new ErrorPacket("You're not in a party!"));
            return;
        }

        if (!party.isLeader(packet.getLeader())) {
            event.setResponse(new ErrorPacket("You must be the leader to invite players to your party!"));
            return;
        }

        if (party.getInvitedPlayers().contains(packet.getToInvite())) {
            event.setResponse(new ErrorPacket(packet.getToInvite().getName() + " has already been invited to your party!"));
            return;
        }

        if (party.getMembers().contains(packet.getToInvite())) {
            event.setResponse(new ErrorPacket(packet.getToInvite().getName() + " is already in your party!"));
            return;
        }

        System.out.println(packet.getServer());

        boolean onSkywars = false;

        for (NirvanaServer server : servers) {
            if (server.getId().equalsIgnoreCase(packet.getServer())) {
                onSkywars = true;
                break;
            }
        }

        if (!(onSkywars)) {
            for (LobbyServer lobby : lobbies) {
                if (lobby.getId().equalsIgnoreCase(packet.getServer())) {
                    onSkywars = true;
                    break;
                }
            }
        }

        if (!(onSkywars)) {
            event.setResponse(new ErrorPacket(packet.getToInvite().getName() + " isn't playing SkyWars!"));
            return;
        }

        event.setResponse(new PartyInfoPacket(party));
        network.sendPacket(event.getPacket(), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
        network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.INVITE_PLAYER, packet.getToInvite()), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
        party.getInvitedPlayers().add(packet.getToInvite());
    }

    @PacketListener({PlayerDisconnectPacket.class})
    public void onPlayerDisconnectPacketReceiveEvent(PacketReceiveEvent event) {
        PlayerDisconnectPacket packet = (PlayerDisconnectPacket) event.getPacket();
        PartyMember member = new PartyMember(packet.getName(), packet.getPlayerUuid());
        Party party = partyMemberMap.get(member);

        if (party != null) {
            if (party.isLeader(member)) {
                for (PartyMember other : party.getMembers()) {
                    partyMemberMap.remove(other);
                }

                parties.remove(party.getUuid());
                partyToUuidMap.remove(party);

                event.setResponse(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.DISBAND, member));
                network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.DISBAND, member), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
            } else {
                System.out.println("Player not a leader fucking removing");
                party.getMembers().remove(member);
                partyMemberMap.remove(member);
                network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_LEAVE, new PartyMember(packet.getName(), packet.getPlayerUuid())), NirvanaChannels.SLAVE_CHANNEL, RabbitNetworkDeliveryType.CLUSTER);
            }
        }

    }

    @PacketListener({PlayerSwitchServerPacket.class})
    public void onPlayerSwitchServerPacketReceiveEvent(PacketReceiveEvent event) {
        PlayerSwitchServerPacket packet = (PlayerSwitchServerPacket) event.getPacket();
        PartyMember member = new PartyMember(packet.getName(), packet.getPlayerUuid());
        Party party = partyMemberMap.get(member);

        if (party != null) {
            boolean onSkywars = false;

            for (NirvanaServer server : servers) {
                if (server.getId().equalsIgnoreCase(packet.getServer())) {
                    onSkywars = true;
                    break;
                }
            }

            if (!(onSkywars)) {
                for (LobbyServer lobby : lobbies) {
                    if (lobby.getId().equalsIgnoreCase(packet.getServer())) {
                        onSkywars = true;
                        break;
                    }
                }
            }

            if (!(onSkywars)) {
                System.out.println(packet.getServer());
                if (party.isLeader(member)) {
                    for (PartyMember other : party.getMembers()) {
                        partyMemberMap.remove(other);
                    }

                    parties.remove(party.getUuid());
                    partyToUuidMap.remove(party);

                    event.setResponse(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.DISBAND, member));
                    network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.DISBAND, member), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
                } else {
                    System.out.println("Player not a leader fucking removing from SWITCH");
                    partyMemberMap.remove(member);
                    party.getMembers().remove(member);
                    network.sendPacket(new PartyUpdatePacket(party, PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_LEAVE, new PartyMember(packet.getName(), packet.getPlayerUuid())), NirvanaChannels.SLAVE_CHANNEL, RabbitNetworkDeliveryType.CLUSTER);
                }
            } else {
                System.out.println("PLAYER IS STILL NOT SKYWARS! NOT REMOVING!");
            }
        }

    }

    @PacketListener({RequestPlayerCountPacket.class})
    public void onRequestPlayerCountPacketReceiveEvent(PacketReceiveEvent event) {
        int count = 0;

        for (NirvanaServer server : servers) {
            if (server.getStatus() != NirvanaServerStatus.OFFLINE) {
                count += server.getPlayers();
            }
        }

        for (LobbyServer server : lobbies) {
            count+=server.getPlayers();
        }

        NirvanaServer server = new NirvanaServer("TOTAL");
        server.setType(NirvanaServerType.PENDING);
        server.setPlayers(count);

        event.setResponse(new ServerInfoPacket(server));
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
                local.setPlayers(server.getPlayers());
                local.setMaxPlayers(server.getMaxPlayers());

                if (server.getStatus() == NirvanaServerStatus.OFFLINE) {
                    local.setPlayers(0);
                }

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

        //System.out.println("Packet received..");

        if (type == NirvanaServerType.PENDING) {
            System.out.println("Cannot return pending server");
            return;
        }

        if (type == NirvanaServerType.LOBBY) {
          //  System.out.println("The type is of lobby..");
           // System.out.println("Iterating over lobby types.");
            Collections.shuffle(lobbies);
            for (LobbyServer server : lobbies) {
               // System.out.println("Found lobby");
                if (server.getPlayers() + count <= server.getMaxPlayers()) {
                   // System.out.println("Lobby has enough player room left");
                    event.setResponse(new LobbyServerStatusPacket(server));
                   // System.out.println("Sent lobby response");
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
                                NirvanaServer copy = getById(check.getId());
                                
                                if (copy != null) {
                                    if (copy.getType() == type && copy.getPlayers() + count <= copy.getMaxPlayers() && (copy.getStatus() == NirvanaServerStatus.WAITING_FOR_PLAYERS || copy.getStatus() == NirvanaServerStatus.STARTING)) {
                                        event.setResponse(new ServerInfoPacket(copy));
                                        copy.setPlayers(copy.getPlayers() + 1);
                                        QueuePacketFragment fragment = QueuePacketFragment.getByPlayer(((ServerSelectPacket) event.getPacket()).getPlayers().get(0), queue);
                                        if (fragment != null) {
                                            queue.remove(fragment);
                                        }
                                        return;
                                    }
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
        new Nirvana(new RedisNetwork(new RedisNetworkConfiguration("142.44.138.178")));
    }

}
