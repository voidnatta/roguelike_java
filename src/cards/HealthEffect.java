package cards;

import entities.Player;

public class HealthEffect implements CardEffect {
    double amount;
    boolean rare;

    public HealthEffect(boolean _rare, double _amount) {
        amount = _amount;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.tryAddHealth((int) amount);
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
