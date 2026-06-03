import java.awt.*;

abstract public class Entity {
    protected Game game;

    double x, y;
    double vx, vy;

    int width, height;

    boolean destroyed = false;

    public Entity(Game _game) {
        game = _game;
    }

    abstract void update(double delta);
    abstract void render(Graphics g);
    public abstract void hit(Entity other);

    boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }
}
