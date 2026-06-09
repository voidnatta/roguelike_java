package entities;

import audio.SoundManager;
import core.Game;
import core.GameConfig;
import graphics.Assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class EnemyNPC extends Enemy {
    double cooldownTimer = 0;

    double maxSpeed = 100;
    double acceleration = 100;
    double friction = 3;

    double velocityY = 0;
    double velocityX = 0;
    double gravity = 500;
    double maxFallSpeed = 600;

    public EnemyNPC(Game _game) {
        super(_game);

        width = 16;
        height = 16;
        health = 2;

        try {
            enemySprite = ImageIO.read(new File("assets/enemy_3.png"));
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
            game.totalEnemiesKilled++;
        }

        TextFalling textFalling =
                new TextFalling(game, 0.6, "-" + String.format("%.2f", amount));

        textFalling.x = x;
        textFalling.y = y;

        game.addEntity(textFalling);

        SoundManager.play("hitHurt");
    }

    @Override
    public void update(double delta) {
        if (bleeding) {
            if (bleedingTimer <= 0) {
                bleedingTimer = bleedingInterval;
                applyDamage((double) bleedingDamage);
            } else {
                bleedingTimer -= delta;
            }
        }

        velocityY -= gravity * delta;

        if (velocityY <= -maxFallSpeed) {
            velocityY = -maxFallSpeed;
        }

        double targetX = game.player.x;
        double dx = targetX - x;
        double distance = Math.sqrt(dx * dx);

        if (distance > 0.001) {

            double desiredVelX = (dx / distance) * maxSpeed;

            double steerX = desiredVelX - velocityX;

            double steerMagnitude =
                    Math.sqrt(steerX * steerX);

            double maxForce = acceleration;

            if (steerMagnitude > maxForce) {
                steerX = (steerX / steerMagnitude) * maxForce;
            }

            velocityX += steerX * delta;
        }

        velocityX *= Math.max(0, 1.0 - friction * delta);
        velocityY *= Math.max(0, 1.0 - friction * delta);

        velocityY -= gravity * delta;
        y -= velocityY * delta;
        x += velocityX * delta;

        if (y >= GameConfig.SCREEN_HEIGHT - 37) {
            y = GameConfig.SCREEN_HEIGHT - 37;
        }

        cooldownTimer -= delta;
    }

    @Override
    public void render(Graphics g) {
        BufferedImage currentSprite = enemySprite;

        g.drawImage(currentSprite, (int) x, (int) y, null);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Player player && cooldownTimer <= 0.0) {
            player.applyDamage(10.0);

            cooldownTimer = 1.0;

            double dx = player.x - x;
            double dy = player.y - y;

            double len = Math.sqrt(dx * dx + dy * dy);

            player.applyKnockback(
                    dx / len * 850,
                    250
            );

            SoundManager.play("hitHurt");

            if (health <= 0) {
                destroyed = true;
                game.enemiesKilled++;
            }
        }
    }
}