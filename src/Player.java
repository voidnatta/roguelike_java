import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player extends Entity {
    double speed = 250;
    int health = 100;

    public int maxHealth = 100;

    double velocityY = 0;
    double velocityX = 0;
    double gravity = 1400;
    double maxFallSpeed = 900;
    double jumpForce = 400;
    double fallGravityMultiplier = 4;
    double jumpHoldGravityMultiplier = 1.1;

    BufferedImage playerSprite;
    BufferedImage playerDeadSprite;
    BufferedImage playerHitSprite;

    double shootTimer = 0;
    double shootRate = 0.8;//0.8
    double hitTimer = -1.0;

    final int widthOffset = 4;
    final int heightOffset = 4;

    boolean gotHit;

    public Player(Game _game) {
        super(_game);

        width = 16;
        height = 16;

        try {
            playerSprite = ImageIO.read(new File("assets/player.png"));
            playerDeadSprite = ImageIO.read(new File("assets/player_dead.png"));
            playerHitSprite = ImageIO.read(new File("assets/player_hit.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void update(double delta) {
        if (isAlive()) {
            double axisX = 0;

            if (Input.isDown(KeyEvent.VK_A)) axisX -= 1;
            if (Input.isDown(KeyEvent.VK_D)) axisX += 1;

            double moveVelocityX = axisX * speed;
            velocityX += (moveVelocityX - velocityX) * 22 * delta;

            if (Input.isKeyJustPressed(KeyEvent.VK_SPACE) && canJump()) {
                velocityY = jumpForce;
                SoundManager.play("jump");
            }

            shootTimer -= delta;

            if (Input.isMouseDown(MouseEvent.BUTTON1) && shootTimer <= 0 && game.canShoot()) {
                spawnProjectile();
                shootTimer = shootRate * game.baseStats.playerFireRateMultiplier;

                SoundManager.play("laserShoot");
            }

            hitTimer -= delta;
        }


        if (velocityY <= -maxFallSpeed) {
            velocityY = -maxFallSpeed;
        }

        double currentGravity = gravity;

        if (velocityY > 0) {

            if (Input.isDown(KeyEvent.VK_SPACE)) {
                currentGravity *= jumpHoldGravityMultiplier;
            } else {
                currentGravity *= 2.5;
            }

        } else if (velocityY < 0) {
            currentGravity *= fallGravityMultiplier;
        }

        velocityY -= currentGravity * delta;

        y -= velocityY * delta;
        x += velocityX * delta;

        if (y >= GameConfig.SCREEN_HEIGHT - 37) {
            y = GameConfig.SCREEN_HEIGHT - 37;

            if (velocityY < 0) {
                velocityY = 0;
            }
        }

        if (x <= 0) {
            x = 0;
        }

        if (x >= GameConfig.SCREEN_WIDTH - width) {
            x = GameConfig.SCREEN_WIDTH - width;
        }
    }

    void spawnProjectile() {
        double startX = x + 6;
        double startY = y - 6;

        double deltaX = Input.mousePosition.x - startX;
        double deltaY = Input.mousePosition.y - startY;

        double angle = Math.atan2(deltaY, deltaX);

        double velX = Math.cos(angle);
        double velY = Math.sin(angle);

        game.addEntity(new PlayerProjectile(game, startX, startY, velX, velY, game.baseStats));
    }

    public void applyDamage(Double amount) {
        health -= amount;
        if (health <= 0) {
            //destroyed = true;
            IO.println("END OF GAME!");
        }

        TextFalling textFalling = new TextFalling(game, 0.6, "-" + String.format("%.2f", amount));
        textFalling.x = 50;
        textFalling.y = 19;

        game.addEntity(textFalling);
    }

    public void applyKnockback(double amountX, double amountY) {
        velocityX += amountX;
        velocityY += amountY;
    }

    public void tryAddHealth(int amount) {
        health = Math.min(health + amount, maxHealth);
    }

    public void addMaxHealth(int amount) {
        maxHealth += amount;
    }

    boolean canJump() {
        return y >= GameConfig.SCREEN_HEIGHT - 38;
    }

    @Override
    void render(Graphics g) {
        g.setColor(Color.BLUE);
        //g.fillRect((int)x, (int)y, width, height);
        if (isAlive()) {
            if (hitTimer < 0) {
                g.drawImage(playerSprite, (int)x, (int)y, width, height, null);
            } else {
                g.drawImage(playerHitSprite, (int)x, (int)y, width, height, null);
            }
        } else {
            g.drawImage(playerDeadSprite, (int)x, (int)y, width + widthOffset, height + heightOffset, null);
        }
    }

    boolean isAlive() {
        return health > 0;
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof EnemyProjectile _p || other instanceof Enemy enemy) {
            gotHit = true;
            hitTimer = 0.2;
        }
    }
}