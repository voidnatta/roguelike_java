package cards;

import entities.Player;

import java.awt.*;

public class Card {
    public String name;
    public String description;

    public CardEffect effect;

    public Rectangle bounds;

    public double hoverProgress = 0.0; // 0.0 = normal, 1.0 = fully hovered

    public Card(String _name, String _description, CardEffect _effect) {
        name = _name;
        description = _description;
        effect = _effect;

        bounds = new Rectangle();
    }

    public void updateHover(boolean isHovered, double delta) {
        double speed = 10.0; // Higher = faster transition
        if (isHovered) {
            hoverProgress = Math.min(1.0, hoverProgress + speed * delta);
        } else {
            hoverProgress = Math.max(0.0, hoverProgress - speed * delta);
        }
    }
    public void apply(Player player) {
        effect.apply(player);
    }
}
