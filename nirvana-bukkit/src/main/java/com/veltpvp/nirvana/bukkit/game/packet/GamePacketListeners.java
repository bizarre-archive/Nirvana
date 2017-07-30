package com.veltpvp.nirvana.bukkit.game.packet;

import com.veltpvp.nirvana.bukkit.Nirvana;
import com.veltpvp.nirvana.packet.BootyCallPacket;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.packet.event.PacketListener;
import us.ikari.phoenix.network.redis.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.packet.event.SimpleEventDispatcher;
import us.ikari.phoenix.network.redis.thread.RedisNetworkSubscribeThread;

public class GamePacketListeners {

    private static Nirvana main = Nirvana.getInstance();

    public GamePacketListeners() {
        RedisNetwork network = main.getNetwork();

        network.registerThread(new RedisNetworkSubscribeThread(main.getNetwork(), NirvanaChannels.SLAVE_CHANNEL));
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
        main.setNetworkStatus(main.getLocalNirvanaServer().getStatus());
    }

}
