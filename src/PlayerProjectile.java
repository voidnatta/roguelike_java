public class PlayerProjectile extends BaseProjectile {
    public PlayerProjectile(Game _game, double x, double y, double dirX, double dirY, BaseStats _baseStats) {
        super(_game, x, y, dirX, dirY, _baseStats, 100 * _baseStats.projectileSpeedMultiplier);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Enemy enemy) {
            health--;

            if (health <= 0) {
                destroyed = true;
            }

            double damageApplied = baseDamage * baseStats.damageMultiplier;

            if (baseStats.bleedProjectile) {
                enemy.bleeding = true;
                enemy.bleedingDamage++;
            }

            enemy.applyDamage(damageApplied);
            SoundManager.play("hitHurt3");

            TextFalling textFalling = new TextFalling(game, 0.6, "-" + String.format("%.2f", damageApplied));
            textFalling.x = x;
            textFalling.y = y;

            game.addEntity(textFalling);
        }

        if (other instanceof Ground ground) {
            game.addEntity(new AnimationEntity(game, x - 5, y - 5, Assets.EXPLOSION));

            destroyed = true;
            SoundManager.play("hitHurt");
        }
    }
}
