import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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

    public int currentWave = 1;

    public int enemiesToSpawn;
    public int enemiesSpawned;
    public int enemiesKilled;

    GameState gameState = GameState.GAMEPLAY;
    GameState lastState = GameState.GAMEPLAY;

    boolean isPlayerAlive() {
        return player.health >= 0;
    }

    Player player = new Player(this);
    Ground ground = new Ground(this);

    private final Random random = new Random();

    BaseStats baseStats = new BaseStats();

    ArrayList<Card> cardPool = new ArrayList<>();
    ArrayList<Card> cards = new ArrayList<>();

    BufferedImage gameBuffer = new BufferedImage(320, 180, BufferedImage.TYPE_INT_ARGB);

    double spawnTimer = 0;
    double spawnInterval = 1.0;

    boolean canShoot = false;
    double canShootTimer = 0.5;

    Graphics2D g2;

    void init() {
        startWave(1);
        SoundManager.loop("music_3");

        player.x = (double)GameConfig.SCREEN_WIDTH / 2 - 16;
        player.y = GameConfig.SCREEN_HEIGHT - 37;

        addEntity(player);
        addEntity(ground);

        cardPool.add(new Card("Furious", "+15% damage",
                new DamageEffect(false, 1.15)));
        cardPool.add(new Card("Resistance", "+1 Projectile\nHP",
                new ProjectHealthEffect(true, 1)));
        cardPool.add(new Card("I'm Late", "+10% Projectile\nSpeed",
                new SpeedEffect(false, 1.10)));
        cardPool.add(new Card("Berserk", "+35% damage",
                new DamageEffect(true, 1.35)));
        cardPool.add(new Card("I'm Too Late", "+25% Projectile\nSpeed",
                new SpeedEffect(true, 1.25)));
        cardPool.add(new Card("Hope", "+50 HP",
                new HealthEffect(false, 50)));
        cardPool.add(new Card("Hope+", "+100 HP",
                new HealthEffect(false, 100)));
        cardPool.add(new Card("Save me", "+25% HP of Max \nHP",
                new MultiplyHealthEffect(false, 0.25)));
        cardPool.add(new Card("Save me+", "+50% HP of Max \nHP",
                new MultiplyHealthEffect(true, 0.50)));
        cardPool.add(new Card("Stronger", "+25 Max HP",
                new MaxHealthEffect(false, 25)));
        cardPool.add(new Card("Stronger+", "+50 Max HP",
                new MaxHealthEffect(true, 50)));
        cardPool.add(new Card("Pull the trigger", "+10% Fire rate",
                new FasterShooterEffect(false, 1-0.10)));
        cardPool.add(new Card("Pull the trigger+", "+20% Fire rate",
                new FasterShooterEffect(true, 1-0.20)));
        cardPool.add(new Card("Bleeding", "Enemy gets\n+1 damage \nevery second.\n(Can stack)",
                new BleedEffect(false)));

        generateLevelUpCards();
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

            return;
        }

        if (gameState == GameState.LEVEL_UP) {
            if (Input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                lastState = gameState;
                gameState = GameState.PAUSED;
            }

            int mouseX = Input.screenMousePosition.x;
            int mouseY = Input.screenMousePosition.y;

            for (Card card : cards) {
                boolean isHovered = card.bounds.contains(mouseX, mouseY);
                card.updateHover(isHovered, delta);
            }

            handleCardSelection();
            return;
        }

        if (gameState == GameState.GAMEPLAY) {
            // pause
            if (Input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                lastState = gameState;
                gameState = GameState.PAUSED;
            }

            if (canShootTimer <= 0) {
                canShoot = true;
            }
            else {
                canShootTimer -= delta;
            }


            for (int i = 0; i < entities.size(); i++) {
                entities.get(i).update(delta);
            }

            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {

                    if (!(entities.get(i) instanceof Enemy a))
                        continue;

                    if (!(entities.get(j) instanceof Enemy b))
                        continue;

                    double centerAX = a.x + a.width / 2.0;
                    double centerAY = a.y + a.height / 2.0;

                    double centerBX = b.x + b.width / 2.0;
                    double centerBY = b.y + b.height / 2.0;

                    double dx = centerBX - centerAX;
                    double dy = centerBY - centerAY;

                    double distSq = dx * dx + dy * dy;

                    if (distSq == 0)
                        continue;

                    double dist = Math.sqrt(distSq);

                    double minDistance =
                            (a.width + b.width) / 2.0;

                    if (dist < minDistance) {

                        double nx = dx / dist;
                        double ny = dy / dist;

                        double overlap = minDistance - dist;

                        double pushForce = overlap * 40.0;

                        a.velX -= nx * pushForce;
                        a.velY -= ny * pushForce;

                        b.velX += nx * pushForce;
                        b.velY += ny * pushForce;
                    }
                }
            }

            spawnTimer -= delta;

            if (spawnTimer <= 0 && enemiesSpawned < enemiesToSpawn && !isBossRound(currentWave)) {
                spawnTimer = spawnInterval;

                int roll = random.nextInt(100);

                Enemy newEnemy;

                if (roll < 80 || currentWave <= 3) {
                    newEnemy = new Enemy(this);
                } else if (roll < 85) {
                    newEnemy = new FollowPlayerEnemy(this);
                } else {
                    newEnemy = new EnemyNPC(this);
                }

                newEnemy.x = random.nextInt(GameConfig.SCREEN_WIDTH - newEnemy.width);
                newEnemy.y = -20;

                entities.add(newEnemy);
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
                generateLevelUpCards();
                gameState = GameState.LEVEL_UP;
            }

            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {
                    Entity a = entities.get(i);
                    Entity b = entities.get(j);

                    if (a instanceof UI) continue;
                    if (b instanceof UI) continue;

                    if (a.intersects(b)) {
                        a.hit(b);
                        b.hit(a);
                    }
                }
            }

            entities.removeIf(e -> e.destroyed);
        }

    }

    void draw(Graphics g) {
        Graphics2D bufferG = gameBuffer.createGraphics();

        bufferG.setColor(new Color(34, 35, 35));
        bufferG.fillRect(0, 0, gameBuffer.getWidth(), gameBuffer.getHeight());

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            if (entity instanceof Ground) continue;
            entity.render(bufferG);

            if (entity instanceof UI) continue;

//            bufferG.setColor(new Color(255, 0, 0));
//            bufferG.drawRect((int)entity.x, (int)entity.y, entity.width, entity.height);
        }

        ground.render(bufferG);

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


    void spawnRandomEnemy() {
        int roll = random.nextInt(100);

        Enemy newEnemy;

        if (roll < 80 || currentWave <= 3) {
            newEnemy = new Enemy(this);
        } else if (roll < 85) {
            newEnemy = new FollowPlayerEnemy(this);
        } else {
            newEnemy = new EnemyNPC(this);
        }

        newEnemy.x = random.nextInt(GameConfig.SCREEN_WIDTH - newEnemy.width);
        newEnemy.y = -20;

        entities.add(newEnemy);
        enemiesSpawned++;
        enemiesToSpawn++;
    }

    boolean isBossRound(int wave) {
        return wave % 5 == 0;
    }

    void startWave(int wave) {
        currentWave = wave;

        enemiesSpawned = 0;
        enemiesKilled = 0;

        spawnTimer = 0;

        // Spawn rate increases gradually
        spawnInterval = Math.max(
                0.17,
                Math.pow(0.86, wave)
        );

        // add new boss each 5 waves
        if (isBossRound(wave)) {
            var boss = new Boss(this);
            boss.x = GameConfig.SCREEN_WIDTH * 0.5;
            boss.y = -48;

            addEntity(boss);
            enemiesToSpawn = 1;
            enemiesSpawned = 1;

            return;
        }

        if (wave <= 15) { // Easy
            enemiesToSpawn = 2 + wave;
        } else if (wave <= 45) { // Medium
            enemiesToSpawn = (int)(17 * Math.pow(1.05, wave - 15));
        } else { // Hard
            enemiesToSpawn = (int)(73 * Math.pow(1.07, wave - 45));
        }
    }

    public boolean canShoot() {
        return canShoot;
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

                    SoundManager.play("powerUp");
                    canShoot = false;
                    canShootTimer = 0.5;
                    break;
                }
            }
        }
    }

    void generateLevelUpCards() {
        cards.clear();

        ArrayList<Card> commons = new ArrayList<>();
        ArrayList<Card> rares = new ArrayList<>();

        for (Card card : cardPool) {
            if (card.effect.isRare()) {
                rares.add(card);
            } else {
                commons.add(card);
            }
        }

        while (cards.size() < 3) {

            // 10% chance for a rare card
            boolean wantRare =
                    random.nextDouble() < 0.10 &&
                            !rares.isEmpty();

            ArrayList<Card> source =
                    wantRare ? rares : commons;

            if (source.isEmpty())
                source = wantRare ? commons : rares;

            Card selected =
                    source.get(random.nextInt(source.size()));

            if (!cards.contains(selected)) {
                cards.add(selected);
            }
        }
    }

    void drawPlayerUI(Graphics g) {
        g.setColor(new Color(240, 246, 240));
        Font font = (Assets.pixelFont != null) ? Assets.pixelFont : g.getFont();

        g.setFont(font.deriveFont(Font.PLAIN, 24.0f));

        g.drawString(
                "WAVE " + currentWave,
                10,
                30
        );

        g.drawString(
                "HP " + Math.clamp(player.health, 0, player.maxHealth) + "/" + player.maxHealth,
                10,
                60
        );

        g.setColor(new Color(34, 35, 35));
        g.fillRect(
                10,
                70,
                (int)(((double) player.health / player.maxHealth) * 200.0),
                20
        );

        int targetWidth = (int)(((double) player.health / player.maxHealth) * 200.0);

        g.setColor(new Color(240, 246, 240));
        g.drawRect(
                10,
                70,
                Math.max(targetWidth, 2),
                20
        );

        g.drawString(
                enemiesKilled + "/" + enemiesToSpawn,
                10,
                120
        );
    }

    void drawCards(Graphics g) {
        int baseWidth = 85 * 2;
        int baseHeight = 120 * 2;
        int spacing = 40;

        int totalWidth = cards.size() * baseWidth + (cards.size() - 1) * spacing;

        int startX = (GameConfig.getRealScreenWidth() - totalWidth) / 2;
        int baseY = (GameConfig.getRealScreenHeight() - baseHeight) / 2;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            int baseLayoutX = startX + i * (baseWidth + spacing);

            double scaleFactor = 1.0 + (0.15 * card.hoverProgress);

            int currentWidth = (int) (baseWidth * scaleFactor);
            int currentHeight = (int) (baseHeight * scaleFactor);

            int x = baseLayoutX - (currentWidth - baseWidth) / 2;
            int y = baseY - (currentHeight - baseHeight) / 2;

            card.bounds.setBounds(x, y, currentWidth, currentHeight);

            g.setColor(new Color(34, 35, 35));
            g.fillRect(x, y, currentWidth, currentHeight);

            g.setColor(new Color(240, 246, 240));
            g.drawRect(x, y, currentWidth, currentHeight);

            Font currentFont = g.getFont();

            float fontSize = (float) (18.0f + (3.0f * card.hoverProgress));
            g.setFont(currentFont.deriveFont(fontSize));

            g.drawString(card.name, x + 5, y + (int)(25 * scaleFactor));

            String[] lines = card.description.split("\n");
            int lineHeight = g.getFontMetrics().getHeight() - 5;
            int textY = y + (int)(75 * scaleFactor);

            for (String line : lines) {
                g.drawString(line, x + 5, textY);
                textY += lineHeight;
            }

            if (card.effect.isRare()) {
                g.drawString("Rare", x + (int)((double) currentWidth / 2 - 20 * scaleFactor), y + (int)(currentHeight - 20 * scaleFactor));
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
