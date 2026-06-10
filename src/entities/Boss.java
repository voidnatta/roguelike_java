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

enum BossState {
    IDLE,
    CLOSE_HANDS,
    THORNS,
    SHOOT_PATTERN
}

public class Boss extends Entity {
    BossState state = BossState.IDLE;
    double stateTimer = 2.0;
    int attackPhase = 0;

    double health = 200.0;

    double shootDuration = .2;
    double shootCooldown = .3;

    double shootDurationTimer = shootDuration;
    double shootCooldownTimer = 0;

    boolean shooting = true;

    double enemiesSpawned = 0;

    double bleedingInterval = 1;
    double bleedingTimer = 0;

    boolean bleeding = false;
    int bleedingDamage = 0;

    double thornSpawnTimer = 0;
    double thornSpawnRate = 0.2;

    int thornIndex = 0;
    int currentThornPhase = 0;

    BufferedImage currentSprite;

    BufferedImage bossIdle;
    BufferedImage bossAngry;
    BufferedImage bossClosehands;

    double velX = 0;
    double velY = 0;

    double maxSpeed = 150;
    double acceleration = 100;
    double friction = 3;

    int drawOffsetY;
    double timer;

    public Boss(Game _game) {
        super(_game);

        width = 48;
        height = 48;

        bleedingTimer = bleedingInterval;
        shootCooldownTimer = shootCooldown;

        try {
            bossIdle = ImageIO.read(new File("assets/boss.png"));
            bossAngry = ImageIO.read(new File("assets/boss_angry.png"));
            bossClosehands = ImageIO.read(new File("assets/boss_closehands.png"));

            currentSprite = bossIdle;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        handleState(delta);
    }

    void handleState(double delta) {
        stateTimer -= delta;

        switch (state) {
            case IDLE -> {
                currentSprite = bossIdle;

                moveToCenter(delta);
                animateOffsetY(delta, 10, 30, 2);

                if(stateTimer <= 0) {
                    if(attackPhase == 0) {
                        attackPhase = 1;
                        changeState(BossState.CLOSE_HANDS, 2.0);
                        enemiesSpawned = 0;
                    } else {
                        attackPhase = 0;
                        changeState(BossState.SHOOT_PATTERN, 5.0);
                    }
                }
            }

            case CLOSE_HANDS -> {
                currentSprite = bossClosehands;
                animateOffsetY(delta, 5, 10, 1);

                while (enemiesSpawned < 4) {
                    enemiesSpawned++;
                    game.spawnRandomEnemy();
                }

                if(stateTimer <= 0) {
                    changeState(BossState.THORNS, 5.0);
                }
            }

            case THORNS -> {
                currentSprite = bossAngry;
                animateOffsetY(delta, 10, 15, 5);
                y = moveTowards(y, 30, 30 * delta);

                switch (currentThornPhase) {
                    case 0 -> {
                        x = moveTowards(x, 0, 50 * delta);

                        if (x < 1) {
                            currentThornPhase = 1;
                        }
                    }
                    case 1 -> {
                        x = moveTowards(x, GameConfig.SCREEN_WIDTH - width, 50.0 * delta);

                        thornSpawnTimer -= delta;

                        if (thornSpawnTimer <= 0 &&
                                thornIndex < (GameConfig.SCREEN_WIDTH - 32) / 16) {

                            Thorn t = new Thorn(game, GameConfig.SCREEN_HEIGHT - 37);
                            t.x = 16.0 * thornIndex;

                            game.addEntity(t);

                            thornIndex++;
                            thornSpawnTimer = thornSpawnRate;
                        }

                        if (x >= GameConfig.SCREEN_WIDTH - width) {
                            currentThornPhase = 2;
                        }
                    }
                    case 2 -> {
                        moveToCenter(delta);
                    }
                }

                if (thornIndex >= (GameConfig.SCREEN_WIDTH - 32) / 16 &&
                        stateTimer <= 0) {
                    currentThornPhase = 0;
                    changeState(BossState.IDLE, 8.0);
                }
            }

            case SHOOT_PATTERN -> {
                currentSprite = bossAngry;

                moveToCenter(delta);
                animateOffsetY(delta, 10, 30, 8);

                if (shooting) {
                    spawnProjectile(x + width * .5 - 8, (y + (height * 0.5) + 20) + drawOffsetY);
                    shootDurationTimer -= delta;

                    if (shootDurationTimer <= 0) {
                        shooting = false;
                        shootDurationTimer = shootCooldown;
                    }
                } else {
                    shootCooldownTimer -= delta;

                    if (shootCooldownTimer <= 0) {
                        shooting = true;
                        shootCooldownTimer = shootDuration;
                    }
                }

                if(stateTimer <= 0) {
                    changeState(BossState.IDLE, 8.0);
                }
            }
        }
    }

    private void changeState(BossState newState, double duration) {
        state = newState;
        stateTimer = duration;

        if (state == BossState.THORNS) {
            thornIndex = 0;
            thornSpawnTimer = 0;
        }
    }

    private void moveToCenter(double delta) {
        if (game.player != null) {

            double targetX = (GameConfig.SCREEN_WIDTH * 0.5) - width * 0.5;
            double targetY = (GameConfig.SCREEN_HEIGHT * 0.5) - 60;

            double dx = targetX - x;
            double dy = targetY - y;

            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 1) {

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
    }

    private void animateOffsetY(double delta, double minY, double maxY, double speed) {
        timer += delta;

        double amplitude = (maxY - minY) / 2.0;
        double center = (minY - maxY) / 2.0;

        drawOffsetY = (int) (center + Math.sin(timer * speed) * amplitude);
    }

    double moveTowards(double current, double target, double maxDelta) {
        if (Math.abs(target - current) <= maxDelta) {
            return target;
        }

        return current + Math.signum(target - current) * maxDelta;
    }

    void spawnProjectile(double _x, double _y) {
        double deltaX = game.player.x - _x;
        double deltaY = game.player.y - _y;

        double angle = Math.atan2(deltaY, deltaX);

        double velX = Math.cos(angle);
        double velY = Math.sin(angle);

        game.addEntity(
                new EnemyProjectile(
                        game,
                        _x,
                        _y,
                        velX,
                        velY,
                        game.baseStats
                )
        );
    }

    public void applyDamage(double amount) {
        if (state != BossState.IDLE) {
            return;
        }

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
    public void render(Graphics g) {
        g.drawImage(currentSprite, (int) x, (int) y + drawOffsetY, null);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Player player) {
            player.applyDamage(2.0);

            double dx = player.x - x;
            double dy = player.y - y;

            double len = Math.sqrt(dx * dx + dy * dy);

            player.applyKnockback(
                    dx / len * 450,
                    10
            );

            SoundManager.play("hitHurt");

            if (health <= 0) {
                destroyed = true;
                game.enemiesKilled++;
            }
        }
    }
}
