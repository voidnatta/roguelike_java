import java.awt.*;
import java.io.File;

public class Assets {
    public static Animation EXPLOSION = new Animation("assets/animation/explosion");

    public static Font pixelFont;

    public static void loadFonts() {
        try {
            File fontFile = new File("assets/fonts/VCR_OSD_MONO_1.001.ttf");
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.PLAIN, 16);
            e.printStackTrace();
        }
    }
}
