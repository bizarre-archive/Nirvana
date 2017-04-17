package us.ikari.nirvana.game;

import us.ikari.nirvana.Nirvana;

public enum GameEventStage {

    NONE,
    FIRST_REFILL,
    SECOND_REFILL,
    DEATHMATCH;

    public static int getDuration(GameEventStage stage) {
        return Nirvana.getInstance().getConfigFile().getInteger("STATE.EVENT." + stage.name() + ".START");
    }

    public static int getCountdown(GameEventStage stage) {
        return Nirvana.getInstance().getConfigFile().getInteger("STATE.EVENT." + stage.name() + ".COUNTDOWN");
    }

    public static long getActiveCountdown(Game game) {
        if (game.getState() == GameState.PLAY) {
            for (GameEventStage stage : values()) {
                if (game.getGameTime().timePassed() > getDuration(stage) && game.getGameTime().timePassed() < (getDuration(stage) + getCountdown(stage))) {
                    return game.getGameTime().timeLeft(getDuration(stage) + getCountdown(stage));
                }
            }
        }
        return 0;
    }

}
