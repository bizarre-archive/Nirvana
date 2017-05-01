package us.ikari.nirvana.game.player;

import lombok.Getter;
import us.ikari.nirvana.Nirvana;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.UUID;

public class GamePlayer {

    private static Nirvana main = Nirvana.getInstance();

    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final GamePlayerData data;

    public GamePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.data = new GamePlayerData();
    }

    public String getKitName() {
        return data.kit() != null ? main.getLangFile().getString("KIT." + data.kit().getIdentifier().toUpperCase() + ".NAME", LanguageConfigurationFileLocale.EXPLICIT) : "N/A";
    }

}
