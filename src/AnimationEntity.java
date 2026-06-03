import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimationEntity extends Entity {
    Animation animation;

    double frameTimer = 0;
    double frameDuration = 0.04;

    int currentFrame = 0;

    public AnimationEntity(Game _game, double _x, double _y, Animation _animation) {
        super(_game);

        x = _x;
        y = _y;
        animation = _animation;

        BufferedImage frame = animation.getFrame(0);

        width = frame.getWidth();
        width = frame.getHeight();
    }

    @Override
    void update(double delta) {
        frameTimer += delta;

        while (frameTimer >= frameDuration) {
            frameTimer -= frameDuration;

            currentFrame++;

            if (currentFrame >= animation.frameCount()) {
                destroyed = true;
                return;
            }
        }
    }

    @Override
    void render(Graphics g) {
        g.drawImage(animation.getFrame(currentFrame), (int)x, (int)y, null);
    }

    @Override
    public void hit(Entity other) {}
}
