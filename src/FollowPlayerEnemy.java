import javax.imageio.ImageIO;
import java.awt.*;
        import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FollowPlayerEnemy extends Enemy {
    double randomXOffset;

    double velX = 0;
    double velY = 0;

    double maxSpeed = 90;
    double acceleration = 130;
    double friction = 3;

    FollowPlayerEnemy(Game _game) {
        super(_game);

        width = 16;
        height = 16;

        health = 3;

        Random random = new Random();
        randomXOffset = random.nextInt(-2, 2);

        bleedingTimer = bleedingInterval;

        try {
            enemySprite = ImageIO.read(new File("assets/enemy_2.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        }

        if (bleeding) {
            if (bleedingTimer <= 0) {
                bleedingTimer = bleedingInterval;
                applyDamage((double) bleedingDamage);
            } else {
                bleedingTimer -= delta;
            }
        }
    }

    @Override
    void render(Graphics g) {
        BufferedImage currentSprite = enemySprite;

        g.drawImage(currentSprite, (int) x, (int) y, null);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Player player) {
            player.applyDamage(5.0);

            double dx = player.x - x;
            double dy = player.y - y;

            double len = Math.sqrt(dx * dx + dy * dy);

            player.applyKnockback(
                    dx / len * 450,
                    150
            );

            SoundManager.play("hitHurt");

            if (health <= 0) {
                destroyed = true;
                game.enemiesKilled++;
            }
        }
    }
}