public class FasterShooterEffect implements CardEffect {
    double amount;
    boolean rare;

    public FasterShooterEffect(boolean _rare, double _amount) {
        amount = _amount;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.baseStats.playerFireRateMultiplier *= amount;
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
