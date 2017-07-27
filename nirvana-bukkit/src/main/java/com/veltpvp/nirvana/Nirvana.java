package com.veltpvp.nirvana;

import com.veltpvp.nirvana.game.*;
import com.veltpvp.nirvana.game.kit.ability.GameKitAbilityListeners;
import com.veltpvp.nirvana.game.player.GamePlayerListeners;
import com.veltpvp.nirvana.game.spectator.GameSpectatorListeners;
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
import us.ikari.azazel.Azazel;
import com.veltpvp.nirvana.game.board.GameBoardAdapter;
import com.veltpvp.nirvana.game.chest.GameChestListeners;
import com.veltpvp.nirvana.game.tab.GameTabAdapter;
import us.ikari.phoenix.gui.PhoenixGui;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFile;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.scoreboard.Aether;
import us.ikari.phoenix.scoreboard.AetherOptions;

import java.io.File;

public class Nirvana extends JavaPlugin implements Listener {

    private static Nirvana instance;
    private static final String WORLD_FILE_NAME = "world";

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
        //network = new RedisNetwork(new RedisNetworkConfiguration(configFile.getStringOrDefault("REDIS.HOST", "localhost")));
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

            new Azazel(this, new GameTabAdapter(this));

            registerBoard();
        }
    }


    private void registerBoard() {
        new Aether(this, new GameBoardAdapter(this), new AetherOptions().hook(true));
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GameListeners(this), this);
        pluginManager.registerEvents(new GamePlayerListeners(this), this);
        pluginManager.registerEvents(game.getLobby().getListeners(), this);
        pluginManager.registerEvents(new GameChestListeners(), this);
        pluginManager.registerEvents(new GameSpectatorListeners(this), this);
        pluginManager.registerEvents(new GameKitAbilityListeners(this), this);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new GameChunkGenerator();
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        if (event.getWorld().getName().equals(WORLD_FILE_NAME)) {
            setGame(new GameLoader(this).getGame());

            for (Entity entity : event.getWorld().getEntities()) {
                entity.remove();
            }
        }
    }

    static {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + WORLD_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Nirvana getInstance() {
        return instance;
    }
}
