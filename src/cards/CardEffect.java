package cards;

import entities.Player;

public interface CardEffect {
    void apply(Player player);
    boolean isRare();
}
