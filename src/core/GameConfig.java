package core;

public class GameConfig {
    public final static int SCREEN_WIDTH = 256;
    public final static int SCREEN_HEIGHT = 180;
    public final static int SCREEN_SCALE = 3;

    public static int getRealScreenWidth() {
        return SCREEN_WIDTH * SCREEN_SCALE;
    }

    public static int getRealScreenHeight() {
        return SCREEN_HEIGHT * SCREEN_SCALE;
    }
}
