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
    double stateTimer = 5.0;
    int attackPhase = 0;

    double health = 100.0;

    double shootTimer = 0;
    double shootRate = 2.5;

    double bleedingInterval = 1;
    double bleedingTimer = 0;

    boolean bleeding = false;
    int bleedingDamage = 0;

    BufferedImage sprite;

    double velX = 0;
    double velY = 0;

    double maxSpeed = 150;
    double acceleration = 100;
    double friction = 3;

    int drawOffsetY;
    double timer;

    public Boss(Game _game) {
        super(_game);

        width = 32;
        height = 32;

        bleedingTimer = bleedingInterval;

        try {
            sprite = ImageIO.read(new File("assets/boss.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void update(double delta) {
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
                moveToCenter(delta);
                animateOffsetY(delta);

                if(stateTimer <= 0) {
                    if(attackPhase == 0) {
                        attackPhase = 1;
                        changeState(BossState.CLOSE_HANDS, 10.0);
                    } else {
                        attackPhase = 0;
                        changeState(BossState.SHOOT_PATTERN, 5.0);
                    }
                }
            }
            case CLOSE_HANDS -> {
                animateOffsetY(delta);

                if(stateTimer <= 0) {
                    changeState(BossState.THORNS, 3.0);
                }
            }
            case THORNS, SHOOT_PATTERN -> {
                animateOffsetY(delta);

                if(stateTimer <= 0) {
                    changeState(BossState.IDLE, 4.0);
                }
            }
        }
    }

    private void changeState(BossState newState, double duration) {
        state = newState;
        stateTimer = duration;
    }

    private void moveToCenter(double delta) {
        if (game.player != null) {

            double targetX = (GameConfig.SCREEN_WIDTH * 0.5) - width;
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

    private void animateOffsetY(double delta) {
        timer += delta;

        double minY = 10;
        double maxY = 30;

        double amplitude = (maxY - minY) / 2.0;
        double center = (minY - maxY) / 2.0;

        drawOffsetY = (int) (center + Math.sin(timer * 3.0) * amplitude);
    }

    public boolean applyDamage(double amount) {
        if (state == BossState.CLOSE_HANDS) {
            IO.println(health);
            return false;
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
        }

        TextFalling textFalling =
                new TextFalling(game, 0.6, "-" + String.format("%.2f", amount));

        textFalling.x = x;
        textFalling.y = y;

        game.addEntity(textFalling);

        SoundManager.play("hitHurt");

        return true;
    }

    @Override
    void render(Graphics g) {
        g.drawImage(sprite, (int) x, (int) y + drawOffsetY, null);
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
