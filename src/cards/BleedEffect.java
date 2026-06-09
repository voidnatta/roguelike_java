package cards;

import entities.Player;

public class BleedEffect implements CardEffect {
    boolean rare;

    public BleedEffect(boolean _rare) {
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.game.baseStats.bleedProjectile = true;
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
