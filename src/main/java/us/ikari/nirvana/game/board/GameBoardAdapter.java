package us.ikari.nirvana.game.board;

import net.minecraft.util.org.apache.commons.lang3.time.DateFormatUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameEventStage;
import us.ikari.nirvana.game.GameState;
import us.ikari.nirvana.game.task.GameStartTask;
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
        return main.getLangFile().getString("SCOREBOARD.TITLE", LanguageConfigurationFileLocale.EXPLICIT);
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {

        if (game.getState() == GameState.LOBBY) {
            return main.getLangFile().getStringList("SCOREBOARD." + (game.hasTask(GameStartTask.class) ? "LOBBY" : "LOBBY_WAITING"), LanguageConfigurationFileLocale.EXPLICIT, game.getAlivePlayers().size(), game.getLobby().getSpawnLocations().size(), game.getGameTime().secondsLeft(main.getConfigFile().getInteger("STATE.LOBBY.COUNTDOWN")) + 1, "SW-1"); //TODO Change last param
        }

        if (game.getState() == GameState.PLAY) {
            int event = (int) (GameEventStage.getActiveCountdown(game));
            return main.getLangFile().getStringList("SCOREBOARD." + (event == 0 ? "GAME" : (game.getRefillStage() == GameEventStage.SECOND_REFILL || game.getRefillStage() == GameEventStage.DEATHMATCH ? "EVENT_DEATHMATCH" : "EVENT_REFILL")), LanguageConfigurationFileLocale.EXPLICIT, DateFormatUtils.format(new Date(), "MM/dd/yyyy"), event >= 60000 ? DurationFormatUtils.formatDuration(event + 1000, "mm:ss") : ((event + 1000) / 1000) + "s", game.getAlivePlayers().size(), player.getStatistic(Statistic.PLAYER_KILLS), game.getMap());
        }

        return null;
    }

}
