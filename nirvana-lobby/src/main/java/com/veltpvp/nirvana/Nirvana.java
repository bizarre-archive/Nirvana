package com.veltpvp.nirvana;

import com.veltpvp.nirvana.board.NirvanaBoardAdapter;
import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.Lobby;
import com.veltpvp.nirvana.lobby.LobbyListeners;
import com.veltpvp.nirvana.lobby.LobbyMenu;
import com.veltpvp.nirvana.lobby.LobbyProfileQueue;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import com.veltpvp.nirvana.lobby.profile.LobbyProfileListeners;
import com.veltpvp.nirvana.packet.BootyCallPacket;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerQueuePacket;
import com.veltpvp.nirvana.packet.ServerSendPlayerPacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServer;
import com.veltpvp.nirvana.packet.lobby.LobbyServerListPacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServerRemovePacket;
import com.veltpvp.nirvana.packet.lobby.LobbyServerStatusPacket;
import com.veltpvp.nirvana.packet.party.PartyMember;
import com.veltpvp.nirvana.packet.party.PartyUpdatePacket;
import com.veltpvp.nirvana.parkour.ParkourListeners;
import com.veltpvp.nirvana.parties.NirvanaParties;
import com.veltpvp.nirvana.tab.NirvanaTabAdapter;
import com.veltpvp.nirvana.util.LocationSerialization;
import com.veltpvp.nirvana.util.NirvanaUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.azazel.Azazel;
import us.ikari.phoenix.gui.PhoenixGui;
import us.ikari.phoenix.gui.menu.PlayerMenu;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFile;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;
import us.ikari.phoenix.network.packet.event.PacketListener;
import us.ikari.phoenix.network.packet.event.PacketReceiveEvent;
import us.ikari.phoenix.network.redis.RedisNetwork;
import us.ikari.phoenix.network.redis.thread.RedisNetworkSubscribeThread;
import us.ikari.phoenix.npc.NPC;
import us.ikari.phoenix.scoreboard.Aether;
import us.ikari.phoenix.scoreboard.AetherOptions;

import java.io.IOException;
import java.util.List;

public class Nirvana extends JavaPlugin implements Listener {

    private static Nirvana instance;

    @Getter private BasicConfigurationFile mainConfig;
    @Getter private LanguageConfigurationFile langConfig;
    @Getter private RedisNetwork network;
    @Getter private Lobby lobby;
    @Getter private String id;
    @Getter private List<LobbyServer> lobbies;
    @Getter private PhoenixGui gui;
    @Getter private Aether aether;
    @Getter private Azazel azazel;
    @Getter private NirvanaDatabase mongo;

    @Override
    public void onEnable() {
        instance = this;

        mainConfig = new BasicConfigurationFile(this, "config", false);
        langConfig = new LanguageConfigurationFile(this, "lang", true);

        id = mainConfig.getString("LOBBY-ID");

        Gamemode.load();

        network = NirvanaParties.getInstance().getNetwork();
        network.registerThread(new RedisNetworkSubscribeThread(network, NirvanaChannels.SLAVE_CHANNEL));
        //network.registerThread(new RedisNetworkSubscribeThread(network, Nitrogen.REDIS_CHANNEL));
        network.registerPacketListener(this);

        mongo = new NirvanaDatabase(this);

        gui = new PhoenixGui(this);

        azazel = new Azazel(this, new NirvanaTabAdapter());
        aether = new Aether(this, new NirvanaBoardAdapter(), new AetherOptions().hook(true));

        network.sendPacket(new LobbyServerStatusPacket(new LobbyServer(id, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), System.currentTimeMillis())), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        NPC.prepare(this);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Animals) {
                    entity.remove();
                }
            }
        }

        registerListeners();

        // registerNametagProvider();
    }

    @Override
    public void onDisable() {
        network.sendPacket(new LobbyServerRemovePacket(id), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);

        for (Gamemode gamemode : Gamemode.getGamemodes()) {
            Location location = gamemode.getNpcLocation();
            if (location != null) {
                mainConfig.getConfiguration().set("GAMEMODES." + gamemode.getId() + ".NPC_LOCATION", LocationSerialization.serializeLocation(location));
                try {
                    mainConfig.getConfiguration().save(mainConfig.getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        network.shutdown();
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (BlockState state : event.getChunk().getTileEntities()) {
            if (state instanceof Sign) {
                Sign sign = (Sign) state;
                if (sign.getLine(0).equalsIgnoreCase("[NPC]")) {
                    Gamemode gamemode = Gamemode.getById(sign.getLine(1).trim());

                    if (gamemode != null && gamemode.getNpcLocation() == null) {
                        Location location = sign.getLocation();
                        BlockFace facing = ((org.bukkit.material.Sign) sign.getData()).getFacing();

                        Location offset = new Location(location.getWorld(), location.getX() + facing.getModX(), location.getY() + facing.getModY(), location.getZ() + facing.getModZ());

                        location = NirvanaUtils.lookAt(location, offset);

                        location.getBlock().setType(Material.AIR);

                        gamemode.setNpcLocation(location);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        network.sendPacket(new LobbyServerStatusPacket(new LobbyServer(id, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), System.currentTimeMillis())), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);

        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " * " + ChatColor.RESET + ChatColor.YELLOW + "We are currently in " + ChatColor.LIGHT_PURPLE + "beta" + ChatColor.YELLOW + ". If you find a bug please " + ChatColor.RED + "report" + ChatColor.YELLOW + " it.");
                player.sendMessage("");
            }
        }.runTaskLaterAsynchronously(this, 20L);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        network.sendPacket(new LobbyServerStatusPacket(new LobbyServer(id, Bukkit.getOnlinePlayers().size() - 1, Bukkit.getMaxPlayers(), System.currentTimeMillis())), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        for (Entity entity : event.getWorld().getEntities()) {
            if (entity instanceof Player) continue;
            entity.remove();
        }

        if (lobby == null) {
            Gamemode.loadLocations();
            lobby = new Lobby(event.getWorld().getSpawnLocation());
        }
    }

    @PacketListener({ServerSendPlayerPacket.class})
    public void onServerSendPlayerPacket(PacketReceiveEvent event) {
        ServerSendPlayerPacket packet = (ServerSendPlayerPacket) event.getPacket();

        for (String playerName : packet.getPlayers()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                LobbyProfile profile = LobbyProfile.getByPlayer(player);
                if (profile != null) {
                    if (profile.getQueue() != null) {
                        player.sendMessage(ChatColor.GREEN + "Sending you to " + packet.getServer() + "!");
                    }
                }
            }
        }
    }

    @PacketListener({ServerQueuePacket.class})
    public void onServerQueuePacket(PacketReceiveEvent event) {
        ServerQueuePacket packet = (ServerQueuePacket) event.getPacket();
        Gamemode gamemode = Gamemode.getById(packet.getType());

        if (gamemode != null) {
            for (String name : packet.getPlayers()) {
                Player player = Bukkit.getPlayer(name);

                if (player != null) {
                    LobbyProfile profile = LobbyProfile.getByPlayer(player);

                    if (profile != null) {
                        profile.setQueue(new LobbyProfileQueue(gamemode.getName(), System.currentTimeMillis()));
                        player.sendMessage(ChatColor.YELLOW + "You've been added to the " + ChatColor.LIGHT_PURPLE + gamemode.getName() + ChatColor.YELLOW + " SkyWars queue.");
                    }
                }
            }
        }

    }

    @PacketListener({PartyUpdatePacket.class})
    public void onPartyUpdatePacketReceiveEvent(PacketReceiveEvent event) {
        PartyUpdatePacket packet = (PartyUpdatePacket) event.getPacket();

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.DISBAND) {
            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    lobby.setupPlayer(player, LobbyProfile.getByPlayer(player));
                    LobbyProfile.getByPlayer(player).getMembers().clear();
                    LobbyProfile.getByPlayer(player).setLeader(false);
                }

            }
            return;
        }

        if (packet.getOptional() != null) {
            Player player = Bukkit.getPlayer(packet.getOptional().getUuid());

            if (player != null) {
                LobbyProfile.getByPlayer(player).getMembers().clear();

                if (packet.getParty().getMembers().contains(packet.getOptional())) {
                    for (PartyMember partyMember : packet.getParty().getMembers()) {
                        System.out.println(partyMember.getName());
                        LobbyProfile.getByPlayer(player).getMembers().put(partyMember.getUuid(), partyMember.getName());
                    }
                }
            }
        }

        if (packet.getType() == PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_LEAVE || packet.getType() == PartyUpdatePacket.PartyUpdateType.REMOVE_PLAYER_KICK || packet.getType() == PartyUpdatePacket.PartyUpdateType.ADD_PLAYER) {
            for (PartyMember member : packet.getParty().getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());

                if (player != null) {
                    LobbyProfile.getByPlayer(player).getMembers().clear();

                    for (PartyMember partyMember : packet.getParty().getMembers()) {
                        System.out.println(partyMember.getName());
                        LobbyProfile.getByPlayer(player).getMembers().put(partyMember.getUuid(), partyMember.getName());
                    }
                }
            }
        }

    }

    /*@PacketListener({PartyUpdatePacket.class}) TODO: reenable l8r
    public void onPartyUpdatePacketReceiveEvent(PacketReceiveEvent event) {
        PartyUpdatePacket packet = (PartyUpdatePacket) event.getPacket();

        for (LobbyProfile profile : LobbyProfile.getProfiles().values()) {
            if (!profile.getMembers().isEmpty()) {
                for (String name : packet.getMembers().values()) {
                    if (profile.getMembers().containsValue(name)) {
                        if (!(packet.getMembers().containsValue(profile.getName()))) {
                            profile.getMembers().clear();
                        }
                    }
                }
            }
        }

        for (UUID uuid : packet.getMembers().keySet()) {
            Player player = Bukkit.getPlayer(packet.getMembers().get(uuid));

            if (player != null) {
                LobbyProfile profile = LobbyProfile.getByPlayer(player);

                if (profile != null) {
                    profile.getMembers().clear();
                    if (packet.getType() != PartyUpdatePacket.UpdateType.DISBAND) {
                        profile.getMembers().putAll(packet.getMembers());
                    }
                }
            }

        }
    }*/

    @PacketListener({BootyCallPacket.class})
    public void onBootyCallPacketReceiveEvent(PacketReceiveEvent event) {
        for (LobbyProfile profile : LobbyProfile.getProfiles().values()) {
            profile.setQueue(null);
            Player player = Bukkit.getPlayer(profile.getName());

            if (player != null) {
                lobby.setupPlayer(player, profile);
                profile.getMembers().clear();
            }

        }
        network.sendPacket(new LobbyServerStatusPacket(new LobbyServer(id, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), System.currentTimeMillis())), NirvanaChannels.APPLICATION_CHANNEL, PacketDeliveryMethod.DIRECT);
    }

    @PacketListener({LobbyServerListPacket.class})
    public void onLobbyServerListPacketReceiveEvent(PacketReceiveEvent event) {
        lobbies = ((LobbyServerListPacket) event.getPacket()).getServers();

        for (PlayerMenu menu : PlayerMenu.getMenus()) {
            if (menu instanceof LobbyMenu) {
                ((LobbyMenu) menu).setLobbies(lobbies);

                for (LobbyServer lobbyServer : lobbies) {
                    System.out.println(lobbyServer.getId() + ":" + lobbyServer.getPlayers());
                }

                Inventory inventory = menu.getPlayer().getOpenInventory().getTopInventory();
                inventory.setContents(menu.getInventory().getContents());
            }
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new NirvanaChunkGenerator();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new LobbyListeners(), this);
        Bukkit.getPluginManager().registerEvents(new LobbyProfileListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ParkourListeners(), this);
    }

    /*
    private void registerNametagProvider() {
        FrozenNametagHandler.registerProvider(new NametagProvider("Nirvana-Lobby Nametag Provider", 2) {
            @Override
            public NametagInfo fetchNametag(Player target, Player viewer) {
                return null;
            }
        });
    }
    */

    public static Nirvana getInstance() {
        return instance;
    }
}
