public class GameConfig {
    final static int SCREEN_WIDTH = 256;
    final static int SCREEN_HEIGHT = 180;
    final static int SCREEN_SCALE = 3;

    static int getRealScreenWidth() {
        return SCREEN_WIDTH * SCREEN_SCALE;
    }

    static int getRealScreenHeight() {
        return SCREEN_HEIGHT * SCREEN_SCALE;
    }
}
