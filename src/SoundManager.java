import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, Clip> soundMap = new HashMap<>();

    public static void init() {
        preloadSound("hitHurt", "assets/sfx/hitHurt.wav");
        preloadSound("hitHurt2", "assets/sfx/hitHurt2.wav");
        preloadSound("jump", "assets/sfx/jump.wav");
        preloadSound("laserShoot", "assets/sfx/laserShoot.wav");
        preloadSound("music_1", "assets/musics/music_1.wav");
        preloadSound("music_2", "assets/musics/music_2.wav");
        preloadSound("music_3", "assets/musics/music_3.wav");
    }

    private static void preloadSound(String name, String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            soundMap.put(name, clip);
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + name);
            e.printStackTrace();
        }
    }

    public static void play(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();
        }
    }

    public static void loop(String name) {
        Clip clip = soundMap.get(name);
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

        float volume = 15.0f / 100f;
        control.setValue((float) (Math.log10(volume == 0 ? 0.0001 : volume) * 20.0));

        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}