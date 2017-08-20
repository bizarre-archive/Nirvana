package com.veltpvp.nirvana.game.packet;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.chest.GameChest;
import com.veltpvp.nirvana.packet.BootyCallPacket;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerInfoPacket;
import com.veltpvp.nirvana.packet.ServerSetTypePacket;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.redis.packet.event.PacketListener;
import us.ikari.phoenix.network.redis.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.thread.RedisNetworkListThread;
import us.ikari.phoenix.network.redis.thread.RedisNetworkSubscribeThread;

public class GamePacketListeners {

    private static Nirvana main = Nirvana.getInstance();

    public GamePacketListeners() {
        RedisNetwork network = main.getNetwork();

        network.registerThread(new RedisNetworkSubscribeThread(main.getNetwork(), NirvanaChannels.SLAVE_CHANNEL, ServerInfoPacket.class.getClassLoader()));
        network.registerThread(new RedisNetworkListThread(main.getNetwork(), Bukkit.getServerName(), ServerInfoPacket.class.getClassLoader()));
        RedisNetworkSubscribeThread thread = (RedisNetworkSubscribeThread) network.getThreadByChannel(NirvanaChannels.SLAVE_CHANNEL);
        while (thread == null || !thread.getPubSub().isSubscribed()) {
            thread = (RedisNetworkSubscribeThread) network.getThreadByChannel(NirvanaChannels.SLAVE_CHANNEL);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        network.registerPacketListener(this);
    }

    @PacketListener({BootyCallPacket.class})
    public void onBootyCallPacketReceiveEvent(PacketReceiveEvent event) {
        System.out.println("Application requested status update, updating status..");

        if (event.getMethod() == PacketDeliveryMethod.DIRECT) {
            main.getLocalNirvanaServer().setPlayers(Bukkit.getOnlinePlayers().size());
            main.getLocalNirvanaServer().setMaxPlayers(Bukkit.getMaxPlayers());

            event.setResponse(new ServerInfoPacket(main.getLocalNirvanaServer()));
        } else {
            main.setNetworkStatus(main.getLocalNirvanaServer().getStatus());
        }
    }

    @PacketListener({ServerSetTypePacket.class})
    public void onServerSetTypePacketReceiveEvent(PacketReceiveEvent event) {
        ServerSetTypePacket packet = (ServerSetTypePacket) event.getPacket();

        if (packet.getId().equalsIgnoreCase(Bukkit.getServerName())) {
            main.getLocalNirvanaServer().setType(packet.getType());

            event.setResponse(new ServerInfoPacket(main.getLocalNirvanaServer()));

            GameChest chest;
            try {
                chest = GameChest.valueOf(packet.getType().name());
            } catch (Exception exception) {
                chest = GameChest.POTPVP;
            }

            System.out.println("GAMEMODE SET TO " + ((ServerSetTypePacket) event.getPacket()).getType().name());
            System.out.println("GAMEMODE CHESTS SET TO " + chest.name());

            if (GameChest.POTPVP.getInstances().size() != chest.getInstances().size()) {
                chest.setInstances(GameChest.POTPVP.getInstances());
                System.out.println("SET THE INSTANCES");
            } else {
                System.out.println(chest.name() + ":" + chest.getInstances().size() + ":" + GameChest.POTPVP.getInstances().size());
            }

        }
    }

}
