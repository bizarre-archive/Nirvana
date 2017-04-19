package us.ikari.nirvana;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameChunkGenerator;
import us.ikari.nirvana.game.GameListeners;
import us.ikari.nirvana.game.GameLoader;
import us.ikari.nirvana.game.board.GameBoardAdapter;
import us.ikari.nirvana.game.chest.GameChestListeners;
import us.ikari.nirvana.game.player.GamePlayerListeners;
import us.ikari.nirvana.game.spectator.GameSpectatorListeners;
import us.ikari.phoenix.gui.PhoenixGui;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFile;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.scoreboard.Aether;

public class Nirvana extends JavaPlugin implements Listener {

    private static Nirvana instance;

    @Getter private RedisNetwork network;
    @Getter private BasicConfigurationFile configFile;
    @Getter private LanguageConfigurationFile langFile;
    @Getter private PhoenixGui phoenixGui;
    @Getter private Game game;

    @Override
    public void onEnable() {
        instance = this;

        configFile = new BasicConfigurationFile(this, "config", true);
        langFile = new LanguageConfigurationFile(this, "lang", true);
        network = new RedisNetwork(new RedisNetworkConfiguration(configFile.getStringOrDefault("REDIS.HOST", "localhost")));
        phoenixGui = new PhoenixGui(this);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.eject();
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) {
                    continue;
                }
                entity.remove();
            }
        }
     }

    public void setGame(Game game) {
        if (this.game == null) {
            this.game = game;
            registerListeners();
            registerBoard();
        }
    }

    private void registerBoard() {
        new Aether(this, new GameBoardAdapter(this));
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GameListeners(this), this);
        pluginManager.registerEvents(new GamePlayerListeners(this), this);
        pluginManager.registerEvents(game.getLobby().getListeners(), this);
        pluginManager.registerEvents(new GameChestListeners(), this);
        pluginManager.registerEvents(new GameSpectatorListeners(this), this);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new GameChunkGenerator();
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        setGame(new GameLoader(this).getGame());

        for (Entity entity : event.getWorld().getEntities()) {
            entity.remove();
        }
    }

    public static Nirvana getInstance() {
        return instance;
    }
}
