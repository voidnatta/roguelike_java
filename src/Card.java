import java.awt.*;

public class Card {
    String name;
    String description;

    CardEffect effect;

    Rectangle bounds;

    public Card(String _name, String _description, CardEffect _effect) {
        name = _name;
        description = _description;
        effect = _effect;

        bounds = new Rectangle();
    }

    public void apply(Player player) {
        effect.apply(player);
    }
}
