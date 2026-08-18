package audio;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, Clip> soundMap = new HashMap<>();

    private static String currentMusic = null;

    public static void init() {
        preloadSound("hitHurt", "assets/sfx/hitHurt.wav");
        preloadSound("hitHurt2", "assets/sfx/hitHurt2.wav");
        preloadSound("hitHurt3", "assets/sfx/hitHurt3.wav");
        preloadSound("explosion", "assets/sfx/explosion.wav");
        preloadSound("explosionPitched", "assets/sfx/explosionPitched.wav");
        preloadSound("jump", "assets/sfx/jump.wav");
        preloadSound("laserShoot", "assets/sfx/laserShoot.wav");
        preloadSound("powerUp", "assets/sfx/powerUp.wav");
        preloadSound("click", "assets/sfx/click.wav");

        preloadSound("music_1", "assets/musics/Juhani Junkala [Retro Game Music Pack] Level 1.wav");
        preloadSound("music_2", "assets/musics/Juhani Junkala [Retro Game Music Pack] Level 3.wav");
        preloadSound("music_3", "assets/musics/Juhani Junkala [Retro Game Music Pack] Ending.wav");
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

        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public static void loop(String name) {
        if (name.equals(currentMusic))
            return;

        Clip clip = soundMap.get(name);

        if (clip == null) {
            return;
        }

        // Stop currently playing music
        if (currentMusic != null) {
            Clip currentClip = soundMap.get(currentMusic);

            if (currentClip != null) {
                currentClip.stop();
                currentClip.setFramePosition(0);
            }
        }

        IO.println(currentMusic);

        // Set volume
        FloatControl control =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float volume = 15.0f / 100f;
        float gain = (float) (Math.log10(volume) * 20.0);

        control.setValue(gain);

        // Start new music
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);

        currentMusic = name;
    }

    public static void stopMusic() {
        if (currentMusic != null) {
            Clip clip = soundMap.get(currentMusic);

            if (clip != null) {
                clip.stop();
                clip.setFramePosition(0);
            }

            currentMusic = null;
        }
    }
}