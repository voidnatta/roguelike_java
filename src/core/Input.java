package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;

public class Input implements KeyListener, MouseListener {

    private static final HashMap<Integer, Boolean> currentKeys = new HashMap<>();
    private static final HashSet<Integer> keysPressedThisFrame = new HashSet<>();
    private static final HashSet<Integer> keysReleasedThisFrame = new HashSet<>();

    private static final HashMap<Integer, Boolean> currentMouseButtons = new HashMap<>();
    private static final HashSet<Integer> mousePressedThisFrame = new HashSet<>();
    private static final HashSet<Integer> mouseReleasedThisFrame = new HashSet<>();

    public static Point mousePosition = new Point();
    public static Point screenMousePosition = new Point();

    private static JPanel panel;

    public Input(JPanel panel) {
        Input.panel = panel;
    }

    /**
     * Call ONCE per frame, preferably at the END of the game loop.
     */
    public static void update() {

        Point currentMousePosition = panel.getMousePosition();

        if (currentMousePosition != null) {
            mousePosition.x = currentMousePosition.x / GameConfig.SCREEN_SCALE;
            mousePosition.y = currentMousePosition.y / GameConfig.SCREEN_SCALE;

            screenMousePosition.x = currentMousePosition.x;
            screenMousePosition.y = currentMousePosition.y;
        }

        keysPressedThisFrame.clear();
        keysReleasedThisFrame.clear();

        mousePressedThisFrame.clear();
        mouseReleasedThisFrame.clear();
    }

    // =========================
    // KEYBOARD
    // =========================

    public static boolean isDown(int key) {
        return currentKeys.getOrDefault(key, false);
    }

    public static boolean isKeyJustPressed(int key) {
        return keysPressedThisFrame.contains(key);
    }

    public static boolean isKeyJustReleased(int key) {
        return keysReleasedThisFrame.contains(key);
    }

    // =========================
    // MOUSE
    // =========================

    public static boolean isMouseDown(int button) {
        return currentMouseButtons.getOrDefault(button, false);
    }

    public static boolean isMouseJustPressed(int button) {
        return mousePressedThisFrame.contains(button);
    }

    public static boolean isMouseJustReleased(int button) {
        return mouseReleasedThisFrame.contains(button);
    }

    // =========================
    // KEY EVENTS
    // =========================

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!currentKeys.getOrDefault(key, false)) {
            keysPressedThisFrame.add(key);
        }

        currentKeys.put(key, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        currentKeys.put(key, false);
        keysReleasedThisFrame.add(key);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // =========================
    // MOUSE EVENTS
    // =========================

    @Override
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();

        if (!currentMouseButtons.getOrDefault(button, false)) {
            mousePressedThisFrame.add(button);
        }

        currentMouseButtons.put(button, true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();

        currentMouseButtons.put(button, false);
        mouseReleasedThisFrame.add(button);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}