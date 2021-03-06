package com.veltpvp.nirvana;

import com.google.common.io.Files;
import com.veltpvp.nirvana.game.Game;
import com.veltpvp.nirvana.game.GameChunkGenerator;
import com.veltpvp.nirvana.game.GameListeners;
import com.veltpvp.nirvana.game.GameLoader;
import com.veltpvp.nirvana.game.board.GameBoardAdapter;
import com.veltpvp.nirvana.game.chest.GameChestListeners;
import com.veltpvp.nirvana.game.kit.ability.GameKitAbilityListeners;
import com.veltpvp.nirvana.game.packet.GamePacketListeners;
import com.veltpvp.nirvana.game.player.GamePlayerListeners;
import com.veltpvp.nirvana.game.spectator.GameSpectatorListeners;
import com.veltpvp.nirvana.game.tab.GameTabAdapter;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerInfoPacket;
import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
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
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.azazel.Azazel;
import us.ikari.phoenix.gui.PhoenixGui;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFile;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.RedisNetworkConfiguration;
import us.ikari.phoenix.scoreboard.Aether;
import us.ikari.phoenix.scoreboard.AetherOptions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Nirvana extends JavaPlugin implements Listener {

    private static Nirvana instance;
    private static final String WORLD_FILE_NAME = "world";
    private static final NirvanaServer NIRVANA_SERVER = new NirvanaServer(Bukkit.getServerName());

    @Getter private RedisNetwork network;
    @Getter private BasicConfigurationFile configFile;
    @Getter private LanguageConfigurationFile langFile;
    @Getter private PhoenixGui phoenixGui;
    @Getter private NirvanaDatabase mongo;
    @Getter private Game game;

    //TODO: Change bukkit.getservername call to configuration defined var

    @Override
    public void onEnable() {
        instance = this;

        configFile = new BasicConfigurationFile(this, "config", false);
        langFile = new LanguageConfigurationFile(this, "lang", true);
        network = new RedisNetwork(new RedisNetworkConfiguration("142.44.138.178"), ServerInfoPacket.class.getClassLoader());
        phoenixGui = new PhoenixGui(this);
        mongo = new NirvanaDatabase(this);

        NIRVANA_SERVER.setType(NirvanaServerType.PENDING);
        setNetworkStatus(NirvanaServerStatus.DEPLOYING);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.setHealth(online.getHealth());
                }
            }
        }.runTaskTimer(this, 2L, 2L);

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

        network.shutdown();
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
        NIRVANA_SERVER.setPlayers(Bukkit.getOnlinePlayers().size());
        NIRVANA_SERVER.setMaxPlayers(Bukkit.getMaxPlayers());

        System.out.println("Server status set to " + status.name());

        network.sendPacket(new ServerInfoPacket(NIRVANA_SERVER), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);
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
            File worldFolder = event.getWorld().getWorldFolder();
            File worldNameFile = new File(worldFolder.getPath() + File.separator + "world-name");

            String name;
            try {
                name = Files.readFirstLine(worldNameFile, Charset.defaultCharset());
            } catch (IOException e) {
                name = "N/A";
            }

            for (int x = -5; x <=5; x++) for (int z = -5; z <= 5; z++) event.getWorld().getChunkAt(x, z);

            setGame(new GameLoader(this, name).getGame());

            for (Entity entity : event.getWorld().getEntities()) {
                entity.remove();
            }
        }
    }

    public static Nirvana getInstance() {
        return instance;
    }
}
