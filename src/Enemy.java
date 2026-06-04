import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Enemy extends Entity {
    double health = 3;

    double shootTimer = 0;
    double shootRate = 2.5;

    double bleedingInterval = 1;
    double bleedingTimer = 0;

    boolean bleeding = false;
    int bleedingDamage = 0;

    BufferedImage enemySprite;
    BufferedImage enemySprite2;

    double randomXOffset;

    double velX = 0;
    double velY = 0;

    double maxSpeed = 120;
    double acceleration = 100;
    double friction = 3;

    Enemy(Game _game) {
        super(_game);

        width = 16;
        height = 16;

        Random random = new Random();

        randomXOffset = random.nextInt(-2, 2);

        bleedingTimer = bleedingInterval;

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

            Random random = new Random();

            if (random.nextInt(0, 100) < 50) {
                SoundManager.play("explosion");
            } else {
                SoundManager.play("explosionPitched");
            }
        }

        TextFalling textFalling =
                new TextFalling(game, 0.6, "-" + String.format("%.2f", amount));

        textFalling.x = x;
        textFalling.y = y;

        game.addEntity(textFalling);

        SoundManager.play("hitHurt");
    }

    @Override
    void update(double delta) {

        if (game.player != null) {

            double targetX = game.player.x + randomXOffset;
            double targetY = game.player.y - randomXOffset;

            double dx = targetX - x;
            double dy = targetY - y;

            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0.001) {

                double desiredVelX = (dx / distance) * maxSpeed;
                double desiredVelY = (dy / distance) * maxSpeed;

                double steerX = desiredVelX - velX;
                double steerY = desiredVelY - velY;

                double steerMagnitude =
                        Math.sqrt(steerX * steerX + steerY * steerY);

                double maxForce = acceleration;

                if (steerMagnitude > maxForce) {
                    steerX = (steerX / steerMagnitude) * maxForce;
                    steerY = (steerY / steerMagnitude) * maxForce;
                }

                velX += steerX * delta;
                velY += steerY * delta;
            }

            velX *= Math.max(0, 1.0 - friction * delta);
            velY *= Math.max(0, 1.0 - friction * delta);

            x += velX * delta;
            y += velY * delta;

            if (y > (GameConfig.SCREEN_HEIGHT) / 2.0) {
                y = (GameConfig.SCREEN_HEIGHT) / 2.0;
                velY = 0;
            }
        }

        shootTimer -= delta;

        if (shootTimer <= 0 && game.isPlayerAlive()) {
            spawnProjectile();
            shootTimer = shootRate;
        }

        if (bleeding) {
            if (bleedingTimer <= 0) {
                bleedingTimer = bleedingInterval;
                applyDamage((double) bleedingDamage);
                IO.println(bleedingDamage);
            } else {
                bleedingTimer -= delta;
            }
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

        game.addEntity(
                new EnemyProjectile(
                        game,
                        startX,
                        startY,
                        velX,
                        velY,
                        new BaseStats()
                )
        );
    }

    @Override
    void render(Graphics g) {
        double difference = Math.signum(game.player.x - x);

        BufferedImage currentSprite =
                (difference >= 0)
                        ? enemySprite
                        : enemySprite2;

        g.drawImage(currentSprite, (int) x, (int) y, null);
    }

    @Override
    public void hit(Entity other) {
    }
}