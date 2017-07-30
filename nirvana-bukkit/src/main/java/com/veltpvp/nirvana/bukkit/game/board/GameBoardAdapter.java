package com.veltpvp.nirvana.bukkit.game.board;

import com.veltpvp.nirvana.bukkit.Nirvana;
import com.veltpvp.nirvana.bukkit.game.GameEventStage;
import com.veltpvp.nirvana.bukkit.game.GameState;
import com.veltpvp.nirvana.bukkit.game.player.GamePlayer;
import com.veltpvp.nirvana.bukkit.game.Game;
import net.minecraft.util.org.apache.commons.lang3.time.DateFormatUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import com.veltpvp.nirvana.bukkit.game.task.GameStartTask;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;
import us.ikari.phoenix.scoreboard.scoreboard.Board;
import us.ikari.phoenix.scoreboard.scoreboard.BoardAdapter;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardCooldown;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class GameBoardAdapter implements BoardAdapter {

    private final Nirvana main;
    private final Game game;

    public GameBoardAdapter(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @Override
    public String getTitle(Player player) {
        return main.getLangFile().getString("SCOREBOARD.TITLE", LanguageConfigurationFileLocale.ENGLISH);
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {

        if (game.getState() == GameState.LOBBY) {
            GamePlayer gamePlayer = game.getByPlayer(player);
            return main.getLangFile().getStringListWithArgumentsOrRemove("SCOREBOARD." + (game.hasTask(GameStartTask.class) ? "LOBBY" : "LOBBY_WAITING"), LanguageConfigurationFileLocale.ENGLISH, game.getAlivePlayers().size(), game.getLobby().getSpawnLocations().size(), game.getGameTime().secondsLeft(main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1, "SW-1", (gamePlayer != null ? (gamePlayer.getData().kit() != null ? main.getLangFile().getString("KIT." + gamePlayer.getData().kit().getIdentifier().toUpperCase() + ".NAME", LanguageConfigurationFileLocale.ENGLISH) : null) : null)); //TODO Change last param
        }

        if (game.getState() == GameState.PLAY) {
            int event = (int) (GameEventStage.getActiveCountdown(game));
            return main.getLangFile().getStringList("SCOREBOARD." + (event == 0 ? "GAME" : (game.getRefillStage() == GameEventStage.SECOND_REFILL || game.getRefillStage() == GameEventStage.DEATHMATCH ? "EVENT_DEATHMATCH" : "EVENT_REFILL")), LanguageConfigurationFileLocale.ENGLISH, DateFormatUtils.format(new Date(), "MM/dd/yyyy"), event >= 60000 ? DurationFormatUtils.formatDuration(event + 1000, "mm:ss") : ((event + 1000) / 1000) + "s", game.getAlivePlayers().size(), player.getStatistic(Statistic.PLAYER_KILLS), game.getMap());
        }

        return null;
    }

}
