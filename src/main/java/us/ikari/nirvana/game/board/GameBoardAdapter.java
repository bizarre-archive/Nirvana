package us.ikari.nirvana.game.board;

import org.bukkit.entity.Player;
import us.ikari.phoenix.scoreboard.scoreboard.Board;
import us.ikari.phoenix.scoreboard.scoreboard.BoardAdapter;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardCooldown;

import java.util.List;
import java.util.Set;

public class GameBoardAdapter implements BoardAdapter {

    @Override
    public String getTitle(Player player) {
        return null;
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {
        return null;
    }

}
