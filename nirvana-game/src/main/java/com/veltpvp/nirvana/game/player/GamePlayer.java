package com.veltpvp.nirvana.game.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class GamePlayer {

    private static Nirvana main = Nirvana.getInstance();

    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final GamePlayerData data;

    public GamePlayer(UUID uuid, String name, String displayName) {
        this.uuid = uuid;
        this.name = name;
        this.data = new GamePlayerData(displayName);
    }

    public String getKitName() {
        return data.kit() != null ? main.getLangFile().getString("KIT." + data.kit().getIdentifier().toUpperCase() + ".NAME", LanguageConfigurationFileLocale.ENGLISH) : "N/A";
    }

    public void save() {
        MongoCollection collection = main.getMongo().getPlayers();
        GamePlayerDatabaseFragment fragment = GamePlayerDatabaseFragment.get(uuid, collection);

        int kills = data.kills();
        int deaths = data.spectator() == null ? 0 : 1;

        if (fragment != null) {
            kills += fragment.getTotalKills();
            deaths += fragment.getTotalDeaths();
        }

        Document document = new Document();

        document.put("uuid", uuid.toString());
        document.put("totalKills", kills);
        document.put("totalDeaths", deaths);

        for (NirvanaServerType type : NirvanaServerType.values()) {
            if (type == NirvanaServerType.LOBBY) continue;
            Document field = new Document();

            int wins = 0;
            int losses = 0;

            System.out.println(" ");
            System.out.println(type.name());
            System.out.println(main.getLocalNirvanaServer().getType());
            System.out.println(" ");

            if (type == main.getLocalNirvanaServer().getType()) {
                wins = data.won() ? 1 : 0;
                losses = data.won() ? 0 : 1;
            }

            if (fragment != null) {
                wins += fragment.getWins().getOrDefault(type, 0);
                losses += fragment.getLosses().getOrDefault(type, 0);
            }

            field.put("wins", wins);
            field.put("losses", losses);

            document.put(type.name().toLowerCase(), field);
        }

        collection.replaceOne(eq("uuid", uuid.toString()), document, new UpdateOptions().upsert(true));
    }

    private static class GamePlayerDatabaseFragment {

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

}
