import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Enemy extends Entity {
    double health = 3;

    double shootTimer = 0;
    double shootRate = 2;

    BufferedImage enemySprite;
    BufferedImage enemySprite2;

    Enemy(Game _game) {
        super(_game);
        width = 16;
        height = 16;

        try {
            enemySprite = ImageIO.read(new File("assets/enemy_1.png"));
            enemySprite2 = ImageIO.read(new File("assets/enemy_1_left.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void applyDamage(Double amount) {
        health -= amount;
        if (health <= 0) {
            game.addEntity(new AnimationEntity(game, x, y, Assets.EXPLOSION));

            destroyed = true;
            game.enemiesKilled++;
        }

        SoundManager.play("hitHurt");
    }

    @Override
    void update(double delta) {
        if (game.player != null) {
            x = moveTowards(x, game.player.x, 35 * delta);
            y = moveTowards(y, game.player.y, 35 * delta);
            if (y > (GameConfig.SCREEN_HEIGHT - 30) / 2.0) {
                y = (GameConfig.SCREEN_HEIGHT - 30) / 2.0;
            }
        }

        shootTimer -= delta;

        if (shootTimer <= 0 && game.isPlayerAlive()) {
            spawnProjectile();
            shootTimer = shootRate;
        }
    }

    void spawnProjectile() {
        double startX = x;
        double startY = y;

        double deltaX = game.player.x - startX;
        double deltaY = game.player.y - startY;

        double angle = Math.atan2(deltaY, deltaX);

        double velX = Math.cos(angle);
        double velY = Math.sin(angle);

        game.addEntity(new EnemyProjectile(game, startX, startY, velX, velY, new BaseStats()));
    }

    @Override
    void render(Graphics g) {
        double difference = Math.signum(game.player.x - x);
        BufferedImage currentSprite = (difference >= 0) ? enemySprite : enemySprite2;

        g.drawImage(currentSprite, (int)x, (int)y, null);
    }

    @Override
    public void hit(Entity other) {}

    double moveTowards(double current, double target, double maxDelta) {
        if (Math.abs(target - current) <= maxDelta) {
            return target;
        }

        return current + Math.signum(target - current) * maxDelta;
    }
}
