package com.veltpvp.nirvana.game.kit;

import com.veltpvp.nirvana.game.kit.ability.GameKitAbility;

import java.util.List;

public abstract class PowerfulGameKit implements GameKit {
    public abstract List<GameKitAbility> getAbilities();
}
