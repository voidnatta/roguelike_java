package graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Animation {
    BufferedImage[] frames;

    public Animation(String folderPath) {
        try {
            File folder = new File(folderPath);

            File[] files = folder.listFiles(
                    f -> f.getName().endsWith(".png")
            );

            Arrays.sort(files);

            frames = new BufferedImage[files.length];

            for (int i = 0; i < files.length; i++) {
                frames[i] = ImageIO.read(files[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getFrame(int index) {
        return frames[index];
    }

    public int frameCount() {
        return frames.length;
    }
}
