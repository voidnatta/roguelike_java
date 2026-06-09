package cards;

import entities.Player;

public class MaxHealthEffect implements CardEffect {
    double amount;
    boolean rare;

    public MaxHealthEffect(boolean _rare, double _amount) {
        amount = _amount;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.addMaxHealth((int) amount);
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
