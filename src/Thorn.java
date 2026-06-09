import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Thorn extends Entity {
    BufferedImage sprite;

    double yOffsetTarget;

    double aliveTimer = 3.0;

    public Thorn(Game _game, double _yOffsetTarget) {
        super(_game);
        yOffsetTarget = _yOffsetTarget;

        y = yOffsetTarget + 64;

        width = 16;
        height = 16;

        try {
            sprite = ImageIO.read(new File("assets/thorn.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void update(double delta) {
        aliveTimer -= delta;

        y = Math.max(yOffsetTarget, y - 200 * delta);

        if (aliveTimer <= 0) {
            destroyed = true;
            game.addEntity(new AnimationEntity(game, x, y, Assets.EXPLOSION));

            Random random = new Random();

            if (random.nextInt(0, 100) < 50) {
                SoundManager.play("explosion");
            } else {
                SoundManager.play("explosionPitched");
            }
        }
    }

    @Override
    void render(Graphics g) {
        g.drawImage(sprite, (int)x, (int)y, width, height, null);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Player player) {
            player.applyDamage(1.0);

            player.applyKnockback(
                    100,
                    200
            );

            SoundManager.play("hitHurt2");
        }
    }
}
