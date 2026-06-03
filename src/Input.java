import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Input implements KeyListener, MouseListener {

    static HashMap<Integer, Boolean> currentKeys = new HashMap<>();
    static HashMap<Integer, Boolean> previousKeys = new HashMap<>();

    static HashMap<Integer, Boolean> currentMouseButtons = new HashMap<>();
    static HashMap<Integer, Boolean> previousMouseButtons = new HashMap<>();

    public static Point mousePosition = new Point();
    public static Point screenMousePosition = new Point();

    static JPanel panel;

    Input(JPanel _panel) {
        panel = _panel;
    }

    public static void update() {

        Point currentMousePosition = panel.getMousePosition();

        if (currentMousePosition != null) {
            mousePosition.x = currentMousePosition.x / GameConfig.SCREEN_SCALE;
            mousePosition.y = currentMousePosition.y / GameConfig.SCREEN_SCALE;

            screenMousePosition.x = currentMousePosition.x;
            screenMousePosition.y = currentMousePosition.y;
        }

        previousKeys.clear();
        previousKeys.putAll(currentKeys);

        previousMouseButtons.clear();
        previousMouseButtons.putAll(currentMouseButtons);
    }

    // =========================
    // KEYBOARD
    // =========================

    public static boolean isDown(int key) {
        return currentKeys.getOrDefault(key, false);
    }

    public static boolean isKeyJustPressed(int key) {

        boolean current = currentKeys.getOrDefault(key, false);
        boolean previous = previousKeys.getOrDefault(key, false);

        return current && !previous;
    }

    public static boolean isKeyJustReleased(int key) {

        boolean current = currentKeys.getOrDefault(key, false);
        boolean previous = previousKeys.getOrDefault(key, false);

        return !current && previous;
    }

    // =========================
    // MOUSE
    // =========================

    public static boolean isMouseDown(int button) {
        return currentMouseButtons.getOrDefault(button, false);
    }

    public static boolean isMouseJustPressed(int button) {

        boolean current = currentMouseButtons.getOrDefault(button, false);
        boolean previous = previousMouseButtons.getOrDefault(button, false);

        return current && !previous;
    }

    public static boolean isMouseJustReleased(int button) {

        boolean current = currentMouseButtons.getOrDefault(button, false);
        boolean previous = previousMouseButtons.getOrDefault(button, false);

        return !current && previous;
    }

    // =========================
    // EVENTS
    // =========================

    @Override
    public void keyPressed(KeyEvent e) {
        currentKeys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKeys.put(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        currentMouseButtons.put(e.getButton(), true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentMouseButtons.put(e.getButton(), false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}