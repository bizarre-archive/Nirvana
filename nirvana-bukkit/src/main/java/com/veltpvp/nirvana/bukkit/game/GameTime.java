package com.veltpvp.nirvana.bukkit.game;

public class GameTime {

    private long init;

    public GameTime(long init) {
        this.init = init;
    }

    public GameTime reset() {
        init = System.currentTimeMillis();
        return this;
    }

    public long timeLeft(long goal) {
        return (init + goal) - System.currentTimeMillis();
    }

    public int secondsLeft(long goal) {
        return (int) (timeLeft(goal) / 1000);
    }

    public long timePassed() {
        return (System.currentTimeMillis() - init);
    }

}
