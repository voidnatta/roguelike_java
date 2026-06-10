import core.GamePanel;
import javax.swing.*;

public class Main {
    static void main(String[] args) {
        JFrame frame = new JFrame("Duotone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();

        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}