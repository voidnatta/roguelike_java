package entities;

import core.Game;

import java.awt.*;

abstract public class Entity {
    public Game game;

    public double x, y;
    public double vx, vy;

    public int width, height;

    public boolean destroyed = false;

    public Entity(Game _game) {
        game = _game;
    }

    public abstract void update(double delta);
    public abstract void render(Graphics g);
    public abstract void hit(Entity other);

    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }
}
