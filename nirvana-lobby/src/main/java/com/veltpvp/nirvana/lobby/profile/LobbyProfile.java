package com.veltpvp.nirvana.lobby.profile;

import com.mongodb.client.MongoCollection;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.lobby.LobbyItems;
import com.veltpvp.nirvana.lobby.LobbyProfileQueue;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.party.GetPartyPacket;
import com.veltpvp.nirvana.packet.party.PartyInfoPacket;
import com.veltpvp.nirvana.packet.party.PartyMember;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import com.veltpvp.nirvana.parkour.Parkour;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.phoenix.network.packet.Packet;
import us.ikari.phoenix.network.packet.handler.PacketResponseHandler;
import us.ikari.phoenix.network.rabbit.listener.RabbitNetworkDeliveryType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class LobbyProfile {

    private static Map<UUID, LobbyProfile> profiles = new HashMap<>();

    @Getter private final String name;
    @Getter private boolean hidePlayers;
    @Getter @Setter private LobbyProfileQueue queue;
    @Getter private final Map<UUID, String> members;
    @Getter private GamePlayerDatabaseFragment fragment;
    @Getter @Setter private Parkour parkour;
    @Getter @Setter private boolean leader;

    public LobbyProfile(UUID uuid, String name) {
        this.name = name;
        this.hidePlayers = false;
        this.members = new LinkedHashMap<>();

        GamePlayerDatabaseFragment fragment = GamePlayerDatabaseFragment.get(uuid, Nirvana.getInstance().getMongo().getPlayers());

        if (fragment == null) {
            fragment = GamePlayerDatabaseFragment.getEmptyFragment();
        }

        this.fragment = fragment;

        profiles.put(uuid, this);

        load(uuid);
    }

    public void load(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Nirvana.getInstance().getNetwork().sendPacket(new GetPartyPacket(new PartyMember(name, uuid)), NirvanaChannels.APPLICATION_CHANNEL, RabbitNetworkDeliveryType.DIRECT, new PacketResponseHandler() {
                    @Override
                    public void onResponse(Packet packet) {
                        if (packet instanceof PartyInfoPacket) {
                            PartyInfoPacket partyInfoPacket = (PartyInfoPacket) packet;
                            for (PartyMember member : partyInfoPacket.getParty().getMembers()) {
                                members.put(member.getUuid(), member.getName());
                            }

                            if (partyInfoPacket.getParty().getMembers().get(0).getUuid().equals(uuid)) {
                                leader = true;
                                Player player = Bukkit.getPlayer(uuid);
                                if (player != null) {
                                    player.getInventory().clear();
                                    player.getInventory().setHeldItemSlot(0);

                                    player.getInventory().setItem(0, LobbyItems.INFORMATION_BOOK);
                                    player.getInventory().setItem(8, LobbyItems.PARTY_DISBANDER);
                                    player.updateInventory();
                                }
                            }
                        }
                    }
                });
            }
        }.runTaskAsynchronously(Nirvana.getInstance());
    }

    public UUID getUniqueId() {
        for (UUID uuid : profiles.keySet()) {
            if (profiles.get(uuid).equals(this)) {
                return uuid;
            }
        }
        return UUID.randomUUID();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(name);
    }

    public void setHidePlayers(boolean hidePlayers) {

        Player player = getPlayer();
        if (player != null) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!(other.getName().equalsIgnoreCase(name))) {
                    if (hidePlayers) {
                        player.hidePlayer(other);
                    } else {
                        player.showPlayer(other);
                    }
                }
            }
        }

        this.hidePlayers = hidePlayers;
    }

    public static LobbyProfile getByPlayer(Player player) {
        return getByUuid(player.getUniqueId());
    }

    public static LobbyProfile getByUuid(UUID uuid) {
        return profiles.get(uuid);
    }

    public static class GamePlayerDatabaseFragment {

        @Getter @Setter private int totalKills;
        @Getter @Setter private int totalDeaths;
        @Getter private Map<NirvanaServerType, Integer> wins;
        @Getter private Map<NirvanaServerType, Integer> losses;

        public GamePlayerDatabaseFragment(int totalKills, int totalDeaths, Map<NirvanaServerType, Integer> wins, Map<NirvanaServerType, Integer> losses) {
            this.totalKills = totalKills;
            this.totalDeaths = totalDeaths;
            this.wins = wins;
            this.losses = losses;
        }

        public static GamePlayerDatabaseFragment getEmptyFragment() {
            Map<NirvanaServerType, Integer> map = new HashMap<>();

            for (NirvanaServerType type : NirvanaServerType.values()) {
                map.put(type, 0);
            }

            return  new GamePlayerDatabaseFragment(0, 0, map, map);
        }

        public static GamePlayerDatabaseFragment get(UUID uuid, MongoCollection collection) {
            Document document = (Document) collection.find(eq("_id", uuid.toString())).first();

            if (document != null) {
                int totalKills = document.getInteger("totalKills");
                int totalDeaths = document.getInteger("totalDeaths");

                Map<NirvanaServerType, Integer> wins = new HashMap<>();
                Map<NirvanaServerType, Integer> losses = new HashMap<>();

                for (NirvanaServerType type : NirvanaServerType.values()) {
                    if (document.containsKey(type.name().toLowerCase())) {
                        Document field = (Document) document.get(type.name().toLowerCase());
                        wins.put(type, field.getInteger("wins"));
                        losses.put(type, field.getInteger("losses"));
                    }
                }

                return new GamePlayerDatabaseFragment(totalKills, totalDeaths, wins, losses);
            }

            return null;
        }

    }

    public static Map<UUID, LobbyProfile> getProfiles() {
        return profiles;
    }
}
