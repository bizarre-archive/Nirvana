package com.veltpvp.nirvana.hub;

import com.veltpvp.nirvana.packet.BootyCallPacket;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerSelectPacket;
import com.veltpvp.nirvana.packet.ServerStatusPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.packet.Packet;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.redis.packet.event.PacketListener;
import us.ikari.phoenix.network.redis.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.packet.event.SimpleEventDispatcher;
import us.ikari.phoenix.network.redis.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.redis.thread.RedisNetworkListThread;
import us.ikari.phoenix.network.redis.thread.RedisNetworkSubscribeThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Nirvana {

    private final RedisNetwork network;
    private final List<NirvanaServer> servers;
    private final ExecutorService pool;

    public Nirvana(RedisNetwork network) {
        System.out.println("Nirvana application server launched.");

        this.network = network;
        this.pool = Executors.newCachedThreadPool();
        this.network.registerPacketListener(this);
        this.servers = new ArrayList<>();

        network.registerThread(new RedisNetworkListThread(network, NirvanaChannels.APPLICATION_CHANNEL));
        System.out.println("Sending booty call to all slaves.");
        network.sendPacket(new BootyCallPacket(), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
    }

    @PacketListener({ServerStatusPacket.class})
    public void onServerStatusPacketReceiveEvent(PacketReceiveEvent event) {
        ServerStatusPacket packet = (ServerStatusPacket) event.getPacket();
        NirvanaServer server = packet.getServer();

        NirvanaServer local = getById(server.getId());
        if (local == null) {
            System.out.println("Server added. (ID: " + server.getId() + ", TYPE: " + server.getType() + ", STATUS: " + server.getStatus().name() + ")");

            servers.add(packet.getServer());
        } else {
            local.setStatus(server.getStatus());
            System.out.println("Server status updated. (ID: " + server.getId() + ", TYPE: " + server.getType() + ", STATUS: " + server.getStatus().name() + ")");
        }
    }

    @PacketListener({ServerStatusPacket.class})
    public void onServerSelectPacketReceiveEvent(final PacketReceiveEvent event) {
        final String type = ((ServerSelectPacket)event.getPacket()).getType();

        this.pool.execute(new Runnable() { // Our own makeshift queue
            @Override
            public void run() {
                NirvanaServer selectedServer = null;
                while (selectedServer == null) {
                    for (NirvanaServer server : servers) {
                        if (server.getType().equalsIgnoreCase(type) && server.getStatus() == NirvanaServerStatus.WAITING_FOR_PLAYERS) {
                            selectedServer = server;
                            break;
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                event.setResponse(new ServerStatusPacket(selectedServer));
            }
        });

    }

    public NirvanaServer getById(String id) {
        for (NirvanaServer server : servers) {
            if (server.getId().equalsIgnoreCase(id)) {
                return server;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new Nirvana(new RedisNetwork(new RedisNetworkConfiguration("localhost")));
    }

}
