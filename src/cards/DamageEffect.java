package cards;

import entities.Player;

public class DamageEffect implements CardEffect {
    double multiplier;
    boolean rare;

    public DamageEffect(boolean _rare, double _multiplier) {
        this.multiplier = _multiplier;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.game.baseStats.damageMultiplier *= multiplier;
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
