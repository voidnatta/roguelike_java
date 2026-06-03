import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player extends Entity {
    double speed = 100;

    double velocityY = 0;
    double gravity = 500;
    double maxFallSpeed = 600;
    double jumpForce = 300;
    double fallGravityMultiplier = 2;

    BufferedImage playerSprite;
    BufferedImage playerDeadSprite;
    BufferedImage playerHitSprite;

    double shootTimer = 0;
    double shootRate = 0.1;
    double hitTimer = 0.0;

    BaseStats baseStats = new BaseStats();

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
        if (!isAlive()) return;

        double axisX = 0;

        if (Input.isDown(KeyEvent.VK_A)) axisX -= 1;
        if (Input.isDown(KeyEvent.VK_D)) axisX += 1;

        x += axisX * speed * delta;

        velocityY -= gravity * delta;

        if (velocityY <= -maxFallSpeed) {
            velocityY = -maxFallSpeed;
        }

        if (Input.isDown(KeyEvent.VK_SPACE) && canJump()) {
            velocityY = jumpForce;
            SoundManager.play("jump");
        }

        double currentGravity = gravity;

        if (velocityY < 0) {
            currentGravity *= fallGravityMultiplier;
        }

        velocityY -= currentGravity * delta;
        y -= velocityY * delta;

        if (y >= GameConfig.SCREEN_HEIGHT - 36) {
            y = GameConfig.SCREEN_HEIGHT - 36;
        }

        if (x <= 0) {
            x = 0;
        }

        if (x >= GameConfig.SCREEN_WIDTH - width) {
            x = GameConfig.SCREEN_WIDTH - width;
        }

        shootTimer -= delta;

        if (Input.isMouseDown(MouseEvent.BUTTON1) && shootTimer <= 0) {
            spawnProjectile();
            shootTimer = shootRate;

            SoundManager.play("laserShoot");
        }

        hitTimer -= delta;
    }

    void spawnProjectile() {
        double startX = x + 6;
        double startY = y - 6;

        double deltaX = Input.mousePosition.x - startX;
        double deltaY = Input.mousePosition.y - startY;

        double angle = Math.atan2(deltaY, deltaX);

        double velX = Math.cos(angle);
        double velY = Math.sin(angle);

        game.addEntity(new PlayerProjectile(game, startX, startY, velX, velY, baseStats));
    }

    public void applyDamage(Double amount) {
        game.playerHealth -= amount;
        if (game.playerHealth <= 0) {
            //destroyed = true;
            IO.println("END OF GAME!");
        }
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
            g.drawImage(playerDeadSprite, (int)x, (int)y, width, height, null);
        }
    }

    boolean isAlive() {
        return game.playerHealth > 0;
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof EnemyProjectile _p) {
            gotHit = true;
            hitTimer = 0.2;
        }
    }
}