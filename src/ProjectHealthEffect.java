public class ProjectHealthEffect implements CardEffect {
    double healthAmount;

    public ProjectHealthEffect(double _healthAmount) {
        healthAmount = _healthAmount;
    }

    @Override
    public void apply(Player player) {
        player.baseStats.projectileExtraHealth += healthAmount;
    }
}
