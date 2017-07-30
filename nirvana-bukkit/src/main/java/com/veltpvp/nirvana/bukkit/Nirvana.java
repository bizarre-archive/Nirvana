package com.veltpvp.nirvana.bukkit;

import com.veltpvp.nirvana.bukkit.game.Game;
import com.veltpvp.nirvana.bukkit.game.GameChunkGenerator;
import com.veltpvp.nirvana.bukkit.game.GameListeners;
import com.veltpvp.nirvana.bukkit.game.GameLoader;
import com.veltpvp.nirvana.bukkit.game.board.GameBoardAdapter;
import com.veltpvp.nirvana.bukkit.game.packet.GamePacketListeners;
import com.veltpvp.nirvana.bukkit.game.player.GamePlayerListeners;
import com.veltpvp.nirvana.bukkit.game.spectator.GameSpectatorListeners;
import com.veltpvp.nirvana.bukkit.game.kit.ability.GameKitAbilityListeners;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerStatusPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
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
import com.veltpvp.nirvana.bukkit.game.chest.GameChestListeners;
import com.veltpvp.nirvana.bukkit.game.tab.GameTabAdapter;
import us.ikari.phoenix.gui.PhoenixGui;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFile;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.scoreboard.Aether;
import us.ikari.phoenix.scoreboard.AetherOptions;

import java.io.File;

public class Nirvana extends JavaPlugin implements Listener {

    private static Nirvana instance;
    private static final String WORLD_FILE_NAME = "world";
    private static final NirvanaServer NIRVANA_SERVER = new NirvanaServer(Bukkit.getServerName());

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

        NIRVANA_SERVER.setType(configFile.getString("TYPE"));
        setNetworkStatus(NirvanaServerStatus.DEPLOYING);

        new GamePacketListeners();

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

        setNetworkStatus(NirvanaServerStatus.OFFLINE);
     }

    public void setGame(Game game) {
        if (this.game == null) {
            this.game = game;
            registerListeners();

            new Azazel(this, new GameTabAdapter(this));

            registerBoard();

            setNetworkStatus(NirvanaServerStatus.WAITING_FOR_PLAYERS);
        }
    }

    public NirvanaServer getLocalNirvanaServer() {
        return NIRVANA_SERVER;
    }

    public void setNetworkStatus(NirvanaServerStatus status) {
        NIRVANA_SERVER.setStatus(status);

        System.out.println("Server status set to " + status.name());

        network.sendPacket(new ServerStatusPacket(NIRVANA_SERVER), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);
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
