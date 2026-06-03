import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Ground extends Entity {
    BufferedImage groundSprite;

    private int yOffset = -20;

    Ground(Game _game) {
        super(_game);

        try {
            groundSprite = ImageIO.read(new File("assets/ground.png"));

            width = groundSprite.getWidth();
            height = groundSprite.getHeight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        x = 0;
        y = GameConfig.SCREEN_HEIGHT - 20;
    }

    @Override
    void update(double delta) {

    }

    @Override
    void render(Graphics g) {
        g.drawImage(
                groundSprite,
                (int)x,
                (int)y + yOffset,
                null
        );
    }

    @Override
    public void hit(Entity other) {

    }
}
