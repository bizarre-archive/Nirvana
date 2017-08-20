package com.veltpvp.nirvana.lobby.profile;

import com.mongodb.client.MongoCollection;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.lobby.LobbyProfileQueue;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import com.veltpvp.nitrogen.parties.party.Party;
import com.veltpvp.nitrogen.parties.party.PartyMember;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

        load();
    }

    public void load() {
        Party party = Party.getByUuid(getUniqueId(), Nirvana.getInstance().getNetwork());

        if (party != null) {
            for (PartyMember member : party.getMembers()) {
                members.put(member.getUuid(), member.getName());
            }
        }

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
            Document document = (Document) collection.find(eq("uuid", uuid.toString())).first();

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
