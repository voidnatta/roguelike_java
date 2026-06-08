public class EnemyProjectile extends BaseProjectile {
    public EnemyProjectile(Game _game, double x, double y, double dirX, double dirY, BaseStats _baseStats) {
        super(_game, x, y, dirX, dirY, _baseStats, 75);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Player player) {
            health--;
            player.applyDamage(baseDamage * baseStats.damageMultiplier);

            SoundManager.play("hitHurt2");

            if (health <= 0) {
                destroyed = true;
            }
        }

        if (other instanceof PlayerProjectile projectile) {
            health--;
            projectile.applyDamage(1);

            SoundManager.play("hitHurt");

            if (health <= 0) {
                destroyed = true;
            }
        }

        if (other instanceof Ground ground) {
            destroyed = true;
            SoundManager.play("hitHurt");
            game.addEntity(new AnimationEntity(game, x - 5, y - 5, Assets.EXPLOSION));
        }
    }
}
