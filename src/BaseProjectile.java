import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BaseProjectile extends Entity {
    double health = 1;

    BaseStats baseStats;

    double baseDamage = 1;

    BufferedImage projectileSprite;

    public BaseProjectile(Game _game, double x, double y, double dirX, double dirY, BaseStats _baseStats, double _speed) {
        super(_game);

        this.x = x;
        this.y = y;

        width = 10;
        height = 10;

        vx = dirX * _speed;
        vy = dirY * _speed;

        baseStats = _baseStats;
        health += baseStats.projectileExtraHealth;

        try {
            projectileSprite = ImageIO.read(new File("assets/projectile.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void hit(Entity other) {}

    @Override
    void update(double delta) {
        x += vx * delta;
        y += vy * delta;

        if (x < -width || x > GameConfig.SCREEN_WIDTH || y < -height || y > GameConfig.SCREEN_HEIGHT) {
            destroyed = true;
        }

        if (health <= 0) {
            destroyed = true;
        }
    }

    @Override
    void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.drawImage(projectileSprite, (int)x, (int)y, null);
    }
}
