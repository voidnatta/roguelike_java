package entities;

import core.Game;
import graphics.Assets;
import ui.UI;

import java.awt.*;

public class TextFalling extends Entity implements UI {
    private double timer;
    private final String textToDisplay;

    private final double SPEED = 30;

    public TextFalling(Game _game, double _timeToDelete, String _textToDisplay) {
        super(_game);
        timer = _timeToDelete;
        textToDisplay = _textToDisplay;
    }

    @Override
    public void update(double delta) {
        y += SPEED * delta;
        timer -= delta;

        if(timer <= 0) {
            destroyed = true;
        }
    }

    @Override
    public void render(Graphics g) {}

    @Override
    public void hit(Entity other) {}

    @Override
    public void renderUI(Graphics g) {
        Font font = (Assets.pixelFont != null) ? Assets.pixelFont : g.getFont();

        g.setFont(font.deriveFont(Font.PLAIN, 20.0f));
        g.setColor(new Color(240, 246, 240));

        Point position = game.gameToScreenPosition(new Point((int)x, (int)y));

        g.drawString(textToDisplay, (int)position.getX(), (int)position.getY());
    }
}
