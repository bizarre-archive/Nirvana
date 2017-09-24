package com.veltpvp.nirvana.parkour;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Parkour {

    private static Checkpoint startingCheckpoint;

    @Getter private final long timeStarted;
    @Getter @Setter private Checkpoint checkpoint;

    public Parkour() {
        this.timeStarted = System.currentTimeMillis();
        if (startingCheckpoint != null) {
            checkpoint = startingCheckpoint;
        }
    }

    public static class Checkpoint {
        @Getter private final Location location;
        private final long timeReached;

        public Checkpoint(Location location) {
            this.location = location;
            this.timeReached = System.currentTimeMillis();
        }

        public void ring(Player player) {
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 0);
        }

    }

    public static Checkpoint getStartingCheckpoint() {
        return startingCheckpoint;
    }

    public static void setStartingCheckpoint(Checkpoint startingCheckpoint) {
        Parkour.startingCheckpoint = startingCheckpoint;
    }
}
