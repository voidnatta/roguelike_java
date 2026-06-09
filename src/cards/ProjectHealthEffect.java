package cards;

import entities.Player;

public class ProjectHealthEffect implements CardEffect {
    double healthAmount;
    boolean rare;

    public ProjectHealthEffect(boolean _rare, double _healthAmount) {
        healthAmount = _healthAmount;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.game.baseStats.projectileHealthExtra += healthAmount;
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
