public class MultiplyHealthEffect implements CardEffect {
    double amount;
    boolean rare;

    public MultiplyHealthEffect(boolean _rare, double _amount) {
        amount = _amount;
        rare = _rare;
    }

    @Override
    public void apply(Player player) {
        player.tryAddHealth((int)((double) player.maxHealth * amount));
    }

    @Override
    public boolean isRare() {
        return rare;
    }
}
