package us.ikari.nirvana.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.nirvana.game.kit.type.BomberGameKit;
import us.ikari.nirvana.game.kit.type.DefaultGameKit;
import us.ikari.nirvana.game.lobby.GameLobby;
import us.ikari.nirvana.game.player.GamePlayer;
import us.ikari.nirvana.game.task.GameStartTask;

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

    public Game(List<Location> spawnLocations) {
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
        return state == GameState.LOBBY && players.size() >= 1;
    }

}
