package us.ikari.nirvana.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.nirvana.game.kit.type.*;
import us.ikari.nirvana.game.lobby.GameLobby;
import us.ikari.nirvana.game.player.GamePlayer;
import us.ikari.nirvana.game.task.GameStartTask;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.util.ArrayList;
import java.util.List;

public class Game {

    @Getter private final GameLobby lobby;
    @Getter private final List<GamePlayer> players;
    @Getter private final List<BukkitRunnable> activeTasks;
    @Getter private final List<GameKit> kits;
    @Getter private final GameTime gameTime;
    @Getter @Setter private GameState state;
    @Getter @Setter private GameEventStage refillStage;
    @Getter @Setter private final String map;

    public Game(String map, List<Location> spawnLocations) {
        this.map = map;
        this.lobby = new GameLobby(this, spawnLocations);
        this.players = new ArrayList<>();
        this.activeTasks = new ArrayList<>();
        this.kits = new ArrayList<>();
        this.state = GameState.LOBBY;
        this.refillStage = GameEventStage.NONE;
        this.gameTime = new GameTime(System.currentTimeMillis());

        registerKits();
    }

    private void registerKits() {
        kits.add(new DefaultGameKit());
        kits.add(new BomberGameKit());
        kits.add(new TrapperGameKit());
        kits.add(new MinerGameKit());
        kits.add(new GhostGameKit());
        kits.add(new MerchantGameKit());
        kits.add(new RusherGameKit());
        kits.add(new GeneratorGameKit());
        kits.add(new EndermanGameKit());
    }

    public List<GamePlayer> getAlivePlayers() {
        List<GamePlayer> toReturn = new ArrayList<>();

        for (GamePlayer player : players) {
            if (player.getData().alive()) {
                toReturn.add(player);
            }
        }

        return toReturn;
    }

    public void update() {
        if (state != GameState.END) {
            if (players.size() > 1 && getAlivePlayers().size() == 1) {
                state = GameState.END;

                Player winner = Bukkit.getPlayer(getAlivePlayers().get(0).getName());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (String message : Nirvana.getInstance().getLangFile().getStringList("GAME.WON", LanguageConfigurationFileLocale.EXPLICIT, winner.getDisplayName(), winner.getDisplayName())) {
                        player.sendMessage(message);
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.kickPlayer(Nirvana.getInstance().getLangFile().getString("GAME.GAME_OVER", LanguageConfigurationFileLocale.EXPLICIT, winner.getDisplayName()));
                        }
                        Bukkit.getServer().shutdown();
                    }
                }.runTaskLater(Nirvana.getInstance(), 300);
            }
        }
    }

    public GamePlayer getByPlayer(Player player) {
        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.getUuid().equals(player.getUniqueId())) {
                return gamePlayer;
            }
        }
        return null;
    }

    public GamePlayer getBySpawnLocation(Location location) {
        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.getData().spawnLocation() != null && gamePlayer.getData().spawnLocation().equals(location)) {
                return gamePlayer;
            }
        }
        return null;
    }

    public boolean hasTask(Class<? extends BukkitRunnable> task) {
        for (BukkitRunnable runnable : activeTasks) {
            if (runnable.getClass().equals(task)) {
                return true;
            }
        }
        return false;
    }

    public <T extends BukkitRunnable> T getTask(Class<T> type) {
        for (BukkitRunnable runnable : activeTasks) {
            if (runnable.getClass().equals(type)) {
                return type.cast(runnable);
            }
        }
        return null;
    }

    public boolean shouldStart() {
        return hasEnoughPlayers() && !hasTask(GameStartTask.class);
    }

    public boolean hasEnoughPlayers() {
        return state == GameState.LOBBY && players.size() >= (lobby.getSpawnLocations().size() / 2);
    }

}
