package us.ikari.nirvana.game.kit;

import us.ikari.nirvana.game.kit.ability.GameKitAbility;

import java.util.List;

public abstract class PowerfulGameKit implements GameKit {
    public abstract List<GameKitAbility> getAbilities();
}
