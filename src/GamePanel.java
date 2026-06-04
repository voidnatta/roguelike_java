import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {
    Thread gameThread;
    boolean is_running = true;

    final int WIDTH = GameConfig.SCREEN_WIDTH * 3;
    final int HEIGHT = GameConfig.SCREEN_HEIGHT * 3;

    Game game = new Game();
    Input input = new Input(this);

    GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(input);
        addMouseListener(input);

        SoundManager.init();
        Assets.loadFonts();

        game.init();

        // removed because Input was not working correctly...
        // gameThread = new Thread(this);
        // gameThread.start();

        new Timer(16, e -> {
            game.update(1.0 / 60.0);
            repaint();
            Input.update();
        }).start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (is_running) {

            long now = System.nanoTime();

            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            update(delta);

            repaint();
            if (Input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                System.out.println("ESC " + delta);
            }
            Input.update();

            try {
                Thread.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void update(double delta) {
        game.update(delta);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        game.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

}
