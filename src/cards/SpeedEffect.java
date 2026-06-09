package cards;

import entities.Player;

public class SpeedEffect implements CardEffect {
    double multiplier;
    boolean rare;

    public SpeedEffect(boolean _rare, double _multiplier) {
        this.multiplier = _multiplier;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.game.baseStats.projectileSpeedMultiplier *= multiplier;
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
