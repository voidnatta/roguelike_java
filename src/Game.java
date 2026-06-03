import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

enum GameState {
    LEVEL_UP,
    GAMEPLAY,
    PAUSED
}

public class Game {
    private final ArrayList<Entity> entities = new ArrayList<>();

    public int playerHealth = 100;
    public int playerMaxHealth = 100;

    public int currentWave = 1;

    public int enemiesToSpawn;
    public int enemiesSpawned;
    public int enemiesKilled;

    GameState gameState = GameState.GAMEPLAY;
    GameState lastState = GameState.GAMEPLAY;

    boolean isPlayerAlive() {
        return playerHealth >= 0;
    }

    Player player = new Player(this);
    Ground ground = new Ground(this);

    ArrayList<Card> cards = new ArrayList<>();

    BufferedImage gameBuffer = new BufferedImage(320, 180, BufferedImage.TYPE_INT_ARGB);

    double spawnTimer = 0;
    double spawnInterval = 1.0;

    Graphics2D g2;

    void init() {
        startWave(1);
        SoundManager.loop("music_1");

        player.x = (double)GameConfig.SCREEN_WIDTH / 2 - 25;
        player.y = 0;

        addEntity(player);
        addEntity(ground);

        cards.add(new Card("Damager", "+20% damage", new DamageEffect(1.2)));
        cards.add(new Card("Damager", "+50% damage", new DamageEffect(1.5)));
        cards.add(new Card("Resistance", "+1 Projectile \nhp", new ProjectHealthEffect(1)));
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    void update(double delta) {
        if (gameState == GameState.PAUSED) {
            if (Input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                gameState = lastState;
            }
            Input.update();

            return;
        }

        if (gameState == GameState.LEVEL_UP) {
            if (Input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                lastState = gameState;
                gameState = GameState.PAUSED;
            }

            handleCardSelection();

            Input.update();
            return;
        }

        if (gameState == GameState.GAMEPLAY) {
            // pause
            if (Input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                lastState = gameState;
                gameState = GameState.PAUSED;
            }

            for (int i = 0; i < entities.size(); i++) {
                entities.get(i).update(delta);
            }

            spawnTimer -= delta;

            if (spawnTimer <= 0 && enemiesSpawned < enemiesToSpawn) {
                spawnTimer = spawnInterval;

                Random random = new Random();
                Enemy new_enemy = new Enemy(this);

                new_enemy.x = random.nextInt(GameConfig.SCREEN_HEIGHT);
                new_enemy.y = -20;

                entities.add(new_enemy);

                enemiesSpawned++;
            }

            if (enemiesKilled >= enemiesToSpawn) {
                for (Entity entity : entities) {
                    if (entity instanceof Enemy enemy) {
                        enemy.destroyed = true;
                    }

                    if (entity instanceof EnemyProjectile enemy) {
                        enemy.destroyed = true;
                    }
                }
                gameState = GameState.LEVEL_UP;
            }

            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {
                    Entity a = entities.get(i);
                    Entity b = entities.get(j);

                    if (a.intersects(b)) {
                        a.hit(b);
                        b.hit(a);
                    }
                }
            }

            entities.removeIf(e -> e.destroyed);

            Input.update();
        }
    }

    void draw(Graphics g) {
        Graphics2D bufferG = gameBuffer.createGraphics();

        bufferG.setColor(new Color(34, 35, 35));
        bufferG.fillRect(0, 0, gameBuffer.getWidth(), gameBuffer.getHeight());

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.render(bufferG);
        }

        bufferG.dispose();

        g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        int scale = GameConfig.SCREEN_SCALE;

        g2.drawImage(
                gameBuffer,
                0,
                0,
                gameBuffer.getWidth() * scale,
                gameBuffer.getHeight() * scale,
                null
        );

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            if (entity instanceof UI) {
                ((UI) entity).renderUI(g2);
            }
        }

        drawPlayerUI(g2);

        if (gameState == GameState.LEVEL_UP) {
            drawCards(g2);
        }

        if (gameState == GameState.PAUSED) {
            drawPauseUI(g2);
        }
    }

    void startWave(int wave) {
        enemiesToSpawn = 5 + wave * wave;
        enemiesSpawned = 0;
        enemiesKilled = 0;

        spawnTimer = 0;
        spawnInterval *= 0.95;
    }

    void handleCardSelection() {
        if (Input.isMouseJustPressed(MouseEvent.BUTTON1)) {
            int mouseX = Input.screenMousePosition.x;
            int mouseY = Input.screenMousePosition.y;

            for (Card card : cards) {
                if (card.bounds.contains(mouseX, mouseY)) {
                    card.apply(player);

                    currentWave++;
                    startWave(currentWave);

                    gameState = GameState.GAMEPLAY;

                    break;
                }
            }
        }
    }

    void drawPlayerUI(Graphics g) {
        g.setColor(new Color(240, 246, 240));
        Font font = (Assets.pixelFont != null) ? Assets.pixelFont : g.getFont();

        g.setFont(font.deriveFont(Font.PLAIN, 24.0f));
        g.drawString(
                "HP " + Math.clamp(playerHealth, 0, playerMaxHealth) + "/" + playerMaxHealth,
                10,
                30
        );

        g.setColor(new Color(34, 35, 35));
        g.fillRect(10, 40, (int)(((double)playerHealth / 100.0) * 200.0), 20);

        int targetWidth = (int)(((double)playerHealth / 100.0) * 200.0);
        g.setColor(new Color(240, 246, 240));
        g.drawRect(10, 40, Math.max(targetWidth, 2), 20);

        g.drawString(
                enemiesKilled + "/" + enemiesToSpawn,
                10,
                90
        );
    }

    void drawCards(Graphics g) {
        int cardWidth = 85*2;
        int cardHeight = 120*2;
        int spacing = 40;

        int totalWidth =
                cards.size() * cardWidth +
                        (cards.size() - 1) * spacing;

        int startX = (GameConfig.getRealScreenWidth() - totalWidth) / 2;
        int y = (GameConfig.getRealScreenHeight() - cardHeight) / 2;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            int x = startX + i * (cardWidth + spacing);

            card.bounds.setBounds(x, y, cardWidth, cardHeight);

            g.setColor(new Color(34, 35, 35));
            g.fillRect(x, y, cardWidth, cardHeight);

            g.setColor(Color.WHITE);
            g.drawRect(x, y, cardWidth, cardHeight);

            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(20.0f); // Sets size to 24
            g.setFont(newFont);
            g.drawString(card.name, x + 2, y + 25);

            String[] lines = card.description.split("\n");

            int lineHeight = g.getFontMetrics().getHeight() - 5;
            int textY = y + 75;

            for (String line : lines) {
                g.drawString(line, x + 2, textY);
                textY += lineHeight;
            }
        }
    }

    void drawPauseUI(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, GameConfig.SCREEN_WIDTH * GameConfig.SCREEN_SCALE, GameConfig.SCREEN_HEIGHT * GameConfig.SCREEN_SCALE);

        int xOffset = 50;
        int yOffset = 10;

        g.setColor(Color.WHITE);
        g.drawString("PAUSED", GameConfig.SCREEN_WIDTH * GameConfig.SCREEN_SCALE / 2 - xOffset, GameConfig.SCREEN_HEIGHT * GameConfig.SCREEN_SCALE / 2 - yOffset);
    }

    Point gameToScreenPosition(Point position) {
        return new Point(position.x * GameConfig.SCREEN_SCALE, position.y * GameConfig.SCREEN_SCALE);
    }
}
