package us.ikari.nirvana;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameListeners;
import us.ikari.nirvana.game.GameLoader;
import us.ikari.nirvana.game.player.GamePlayerListeners;
import us.ikari.nirvana.game.state.GameStateListeners;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFile;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;

public class Nirvana extends JavaPlugin {

    private static Nirvana instance;

    @Getter private RedisNetwork network;
    @Getter private BasicConfigurationFile configFile;
    @Getter private LanguageConfigurationFile langFile;
    @Getter private Game game;

    @Override
    public void onEnable() {
        instance = this;

        configFile = new BasicConfigurationFile(this, "config", true);
        langFile = new LanguageConfigurationFile(this, "lang", true);
        network = new RedisNetwork(new RedisNetworkConfiguration(configFile.getStringOrDefault("REDIS.HOST", "localhost")));
        game = new GameLoader(this).getGame();

        registerListeners();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GameListeners(this), this);
        pluginManager.registerEvents(new GamePlayerListeners(this), this);
        pluginManager.registerEvents(new GameStateListeners(), this);
    }

    public static Nirvana getInstance() {
        return instance;
    }
}
