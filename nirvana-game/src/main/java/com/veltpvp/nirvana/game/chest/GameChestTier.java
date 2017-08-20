package com.veltpvp.nirvana.game.chest;

import lombok.Getter;

public enum GameChestTier {
    BASIC(0),
    BUFFED(1),
    OP(2);

    @Getter private final int identifier;

    GameChestTier(int identifier) {
        this.identifier = identifier;
    }

    public static GameChestTier getByIdentifier(int identifier) {
        for (GameChestTier tier : values()) {
            if (tier.getIdentifier() == identifier) {
                return tier;
            }
        }
        return null;
    }

}
