public class DamageEffect implements CardEffect {
    double multiplier;

    public DamageEffect(double _multiplier) {
        this.multiplier = _multiplier;
    }

    @Override
    public void apply(Player player) {
        player.baseStats.damageMultiplier *= multiplier;
    }
}
