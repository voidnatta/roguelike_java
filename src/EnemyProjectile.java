public class EnemyProjectile extends BaseProjectile {
    public EnemyProjectile(Game _game, double x, double y, double dirX, double dirY, BaseStats _baseStats) {
        super(_game, x, y, dirX, dirY, _baseStats, 100);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Player player) {
            health--;
            player.applyDamage(baseDamage * baseStats.damageMultiplier);

            SoundManager.play("hitHurt2");
        }

        if (other instanceof PlayerProjectile projectile) {
            health--;
            projectile.health--;

            SoundManager.play("hitHurt");
        }

        if (other instanceof Ground ground) {
            destroyed = true;
            SoundManager.play("hitHurt");
        }
    }
}
