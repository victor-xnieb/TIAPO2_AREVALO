package controller;

import app.MainApp;
import datastructures.LinkedList;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import model.*;

import java.util.HashSet;
import java.util.Set;

import java.util.concurrent.ThreadLocalRandom;
import model.Bullet;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label healthLabel;

    @FXML
    private Label weaponLabel;

    @FXML
    private Label ammoLabel;

    private MainApp mainApp;
    private Scenario scenario;

    private GameMap gameMap;
    private Image background;

    private Player player;
    private LinkedList<Enemy> enemies;
    private LinkedList<Bullet> bullets;
    private LinkedList<AmmoPickup> ammoPickups;
    private LinkedList<EnemyBullet> enemyBullets;



    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private Image[] enemyFrames;
    private AnimationTimer timer;
    private long lastTimeNanos;

    // posición actual de la mira
    private double mouseX;
    private double mouseY;
    private boolean mouseInside = false;


    // Spawns de enemigos
    private double enemySpawnTimer = 0.0;     // acumula segundos
    private final double enemySpawnInterval = 5.0; // cada 5 segundos aparece uno nuevo
    private final int maxEnemies = 12;        // límite para que no se llene de bichos

    // tiempo de invencibilidad del jugador después de recibir daño (en segundos)
    private double playerHitCooldown = 0.0;


    // Cámara y zoom
    private double zoom = 1.0;
    private double cameraX;
    private double cameraY;



    @FXML
    private void initialize() {
        if (gameCanvas != null) {
            // disparar
            gameCanvas.setOnMousePressed(e -> handleShoot(e.getX(), e.getY()));

            // actualizar mira
            gameCanvas.setOnMouseMoved(e -> {
                mouseX = e.getX();
                mouseY = e.getY();
                mouseInside = true;
            });
            gameCanvas.setOnMouseDragged(e -> {
                mouseX = e.getX();
                mouseY = e.getY();
                mouseInside = true;
            });
            gameCanvas.setOnMouseExited(e -> mouseInside = false);
            gameCanvas.setOnMouseEntered(e -> mouseInside = true);

            // 🔍 zoom con rueda del mouse
            gameCanvas.setOnScroll(e -> {
                double factor = (e.getDeltaY() > 0) ? 1.1 : 0.9;
                zoom *= factor;
                // límites para que no se vuelva loco
                if (zoom < 0.5) zoom = 0.5;
                if (zoom > 2.5) zoom = 2.5;
            });
        }
    }




    private void handleShoot(double mouseXScreen, double mouseYScreen) {
        if (!player.consumeAmmo(1)) {
            return; // sin balas
        }

        double w = gameCanvas.getWidth();
        double h = gameCanvas.getHeight();

        // convertir de pantalla -> mundo (inversa de lo que hicimos en render)
        double worldX = (mouseXScreen - w / 2) / zoom + cameraX;
        double worldY = (mouseYScreen - h / 2) / zoom + cameraY;

        double startX = player.getPosition().x;
        double startY = player.getPosition().y;

        bullets.addLast(new Bullet(startX, startY, worldX, worldY));
        updateHud();
    }





    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    // llamado por MainApp después de inyectar todo
    public void initGame() {
        // cargar fondo
        background = new Image(getClass().getResourceAsStream(scenario.getImagePath()));

        // mapa por tiles
        gameMap = new GameMap(scenario.getTiles(), scenario.getTileSize());

        enemyFrames = new Image[] {
                new Image(getClass().getResourceAsStream("/images/enemigo.png")),
                new Image(getClass().getResourceAsStream("/images/enemigo2.png")),
                new Image(getClass().getResourceAsStream("/images/enemigo3.png")),
                new Image(getClass().getResourceAsStream("/images/enemigo4.png"))
        };


        // ajustar canvas al tamaño del mapa
        gameCanvas.setWidth(gameMap.getWidth());
        gameCanvas.setHeight(gameMap.getHeight());

        player = new Player(gameMap.getWidth() / 2, gameMap.getHeight() / 2);
        enemies = new LinkedList<>();
        bullets = new LinkedList<>();
        ammoPickups = new LinkedList<>();
        enemyBullets = new LinkedList<>();

        spawnScenarioEnemies();

        updateHud();

        startLoop();
    }

    private void spawnScenarioEnemies() {
        switch (scenario) {
            case PLAIN -> {
                enemies.addLast(new Enemy(100, 100, EnemyType.BANDIT_REVOLVER));
                enemies.addLast(new Enemy(600, 150, EnemyType.BANDIT_MELEE));
            }
            case MOUNTAIN -> {
                enemies.addLast(new Enemy(150, 500, EnemyType.OUTLAW_RIFLE));
                enemies.addLast(new Enemy(700, 300, EnemyType.HUNTER));
            }
            case RIVER -> {
                enemies.addLast(new Enemy(100, 400, EnemyType.GUARD));
                enemies.addLast(new Enemy(650, 200, EnemyType.SCOUT));
            }
        }
    }

    private void startLoop() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        lastTimeNanos = System.nanoTime();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double delta = (now - lastTimeNanos) / 1_000_000_000.0;
                lastTimeNanos = now;
                update(delta);
                render(gc);
            }
        };
        timer.start();
    }

    private void update(double delta) {
        if (playerHitCooldown > 0) {
            playerHitCooldown -= delta;
            if (playerHitCooldown < 0) playerHitCooldown = 0;
        }

        handleMovement(delta);
        updateEnemies(delta);
        updateBullets(delta);
        updateEnemyBullets(delta);   // lo añadimos luego
        updateAmmoPickups();
        checkPlayerEnemyCollisions();

        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnInterval) {
            enemySpawnTimer = 0.0;
            if (getEnemyCount() < maxEnemies) {
                spawnRandomEnemy();
            }
        }

        if (player.isDead()) {
            timer.stop();
            mainApp.showMainMenu();
        }

        // cámara sigue al jugador
        cameraX = player.getPosition().x;
        cameraY = player.getPosition().y;

        clampCameraToWorld();

    }

    private void clampCameraToWorld() {
        double worldW = gameMap.getWidth();
        double worldH = gameMap.getHeight();

        double viewW = gameCanvas.getWidth() / zoom;
        double viewH = gameCanvas.getHeight() / zoom;

        double halfW = viewW / 2.0;
        double halfH = viewH / 2.0;

        // Eje X
        if (worldW <= viewW) {
            // El mundo es más pequeño que la pantalla: centramos
            cameraX = worldW / 2.0;
        } else {
            cameraX = Math.max(halfW, Math.min(worldW - halfW, cameraX));
        }

        // Eje Y
        if (worldH <= viewH) {
            cameraY = worldH / 2.0;
        } else {
            cameraY = Math.max(halfH, Math.min(worldH - halfH, cameraY));
        }
    }



    private void updateAmmoPickups() {
        // nueva lista para los que NO se hayan recogido
        LinkedList<AmmoPickup> remaining = new LinkedList<>();

        for (AmmoPickup pickup : ammoPickups) {
            // distancia entre jugador y pickup
            double dx = pickup.getPosition().x - player.getPosition().x;
            double dy = pickup.getPosition().y - player.getPosition().y;
            double dist2 = dx * dx + dy * dy;

            double r = pickup.getRadius() + player.getRadius();

            if (dist2 <= r * r) {
                // lo recogemos
                player.addAmmo(pickup.getAmount());
            } else {
                // sigue en el mapa
                remaining.addLast(pickup);
            }
        }

        ammoPickups = remaining;
        updateHud();
    }




    private void updateBullets(double delta) {
        // mover balas
        for (Bullet bullet : bullets) {
            bullet.update(delta, gameMap);

            if (bullet.isDead()) {
                continue;
            }

            for (Enemy enemy : enemies) {
                if (!enemy.isDead() && bullet.collidesWith(enemy)) {
                    enemy.takeDamage(1);
                    bullet.markDead();

                    // si el enemigo murió, suelta balas
                    if (enemy.isDead()) {
                        spawnAmmoPickup(enemy);
                    }
                    break;
                }
            }
        }



        // eliminar balas muertas
        bullets.removeIf(Bullet::isDead);
        // eliminar enemigos muertos
        enemies.removeIf(Enemy::isDead);
    }


    private void spawnAmmoPickup(Enemy enemy) {
        int amount;

        // aquí decides cuántas balas deja cada tipo de enemigo
        switch (enemy.getType()) {
            case BANDIT_MELEE -> amount = 1;               // enemigo débil
            case BANDIT_REVOLVER, HUNTER, SCOUT -> amount = 2;
            case OUTLAW_RIFLE, GUARD -> amount = 3;       // más poderosos
            default -> amount = 1;
        }

        double x = enemy.getPosition().x;
        double y = enemy.getPosition().y;

        ammoPickups.addLast(new AmmoPickup(x, y, amount));
    }



    private int getEnemyCount() {
        int count = 0;
        for (Enemy ignored : enemies) {
            count++;
        }
        return count;
    }


    private void spawnRandomEnemy() {
        int attempts = 0;

        while (attempts < 50) { // no nos quedamos en bucle infinito
            attempts++;

            // Elegimos una columna y fila aleatoria dentro del mapa (evitando bordes)
            int col = ThreadLocalRandom.current().nextInt(1, gameMap.getCols() - 1);
            int row = ThreadLocalRandom.current().nextInt(1, gameMap.getRows() - 1);

            // Solo nos interesan tiles libres (0)
            if (gameMap.getTile(col, row) != 0) {
                continue;
            }

            double x = (col + 0.5) * gameMap.getTileSize();
            double y = (row + 0.5) * gameMap.getTileSize();

            // Evitar que aparezcan encima del jugador o demasiado cerca
            double dx = x - player.getPosition().x;
            double dy = y - player.getPosition().y;
            double dist2 = dx * dx + dy * dy;

            double minDistance = 200; // pixels
            if (dist2 < minDistance * minDistance) {
                continue;
            }

            EnemyType type = chooseEnemyTypeForScenario();
            enemies.addLast(new Enemy(x, y, type));
            break; // spawn hecho
        }
    }


    private EnemyType chooseEnemyTypeForScenario() {
        return switch (scenario) {
            case PLAIN -> ThreadLocalRandom.current().nextBoolean()
                    ? EnemyType.BANDIT_REVOLVER
                    : EnemyType.BANDIT_MELEE;
            case MOUNTAIN -> ThreadLocalRandom.current().nextBoolean()
                    ? EnemyType.OUTLAW_RIFLE
                    : EnemyType.HUNTER;
            case RIVER -> ThreadLocalRandom.current().nextBoolean()
                    ? EnemyType.GUARD
                    : EnemyType.SCOUT;
        };
    }


    private void handleMovement(double delta) {
        double speed = 140;

        double dx = 0;
        double dy = 0;

        if (pressedKeys.contains(KeyCode.W)) dy -= speed * delta;
        if (pressedKeys.contains(KeyCode.S)) dy += speed * delta;
        if (pressedKeys.contains(KeyCode.A)) dx -= speed * delta;
        if (pressedKeys.contains(KeyCode.D)) dx += speed * delta;

        if (dx != 0 || dy != 0) {
            double newX = player.getPosition().x + dx;
            double newY = player.getPosition().y + dy;

            if (gameMap.canMoveTo(newX, newY, player.getRadius())) {
                player.move(dx, dy);
            }
        }

        if (pressedKeys.contains(KeyCode.Q)) {
            player.switchWeapon();
        }

        // disparo muy simplificado: no implemento balas aún para no alargar
        // pero aquí iría shoot()
        updateHud();
    }

    private void updateEnemies(double delta) {
        for (Enemy enemy : enemies) {
            EnemyBullet shot = enemy.updateTowards(player, delta, gameMap);
            if (shot != null) {
                enemyBullets.addLast(shot);
            }
        }
    }


    private void checkPlayerEnemyCollisions() {
        if (playerHitCooldown > 0) return; // todavía invencible

        for (Enemy enemy : enemies) {
            if (enemy.collidesWith(player)) {
                // daño según el tipo de enemigo
                player.takeDamage(enemy.getType().damage);
                playerHitCooldown = 1.0; // 1 segundo sin volver a recibir daño por contacto
                updateHud();
                break;
            }
        }

        if (player.isDead()) {
            timer.stop();
            mainApp.showMainMenu();
        }
    }


    private void render(GraphicsContext gc) {
        // tamaño actual del canvas
        double w = gameCanvas.getWidth();
        double h = gameCanvas.getHeight();

        // resetear transformaciones y limpiar
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, w, h);

        // ================== MUNDO (coordenadas del juego) ==================
        gc.save();

        // mover origen al centro de la pantalla
        gc.translate(w / 2, h / 2);
        // aplicar zoom
        gc.scale(zoom, zoom);
        // centrar cámara en el jugador
        gc.translate(-cameraX, -cameraY);

        // ---- fondo ----
        gc.drawImage(background, 0, 0, gameMap.getWidth(), gameMap.getHeight());

        // opcional: dibujar tiles sólidos para debugear
        // gc.setFill(Color.color(1, 0, 0, 0.3));
        // for (...) if tile==1 gc.fillRect(...);

        // ---- jugador ----
        gc.setFill(Color.BLUE);
        gc.fillOval(
                player.getPosition().x - player.getRadius(),
                player.getPosition().y - player.getRadius(),
                player.getRadius() * 2,
                player.getRadius() * 2
        );

        // ---- enemigos (animados con frames) ----
        long now = System.nanoTime();
        int frame = (int) ((now / 150_000_000L) % enemyFrames.length);
        Image enemyFrame = enemyFrames[frame];

        for (Enemy enemy : enemies) {
            double x = enemy.getPosition().x;
            double y = enemy.getPosition().y;

            double size = enemy.getRadius() * 4; // ajusta si se ven muy grandes

            gc.drawImage(enemyFrame,
                    x - size / 2,
                    y - size / 2,
                    size,
                    size);
        }

        // ---- pickups de munición ----
        gc.setFill(Color.GOLD);
        for (AmmoPickup pickup : ammoPickups) {
            gc.fillOval(
                    pickup.getPosition().x - pickup.getRadius(),
                    pickup.getPosition().y - pickup.getRadius(),
                    pickup.getRadius() * 2,
                    pickup.getRadius() * 2
            );
        }

        // ---- balas del jugador ----
        gc.setFill(Color.YELLOW);
        for (Bullet bullet : bullets) {
            gc.fillOval(
                    bullet.getPosition().x - bullet.getRadius(),
                    bullet.getPosition().y - bullet.getRadius(),
                    bullet.getRadius() * 2,
                    bullet.getRadius() * 2
            );
        }

        // ---- balas enemigas ----
        gc.setFill(Color.ORANGE);
        for (EnemyBullet bullet : enemyBullets) {
            gc.fillOval(
                    bullet.getPosition().x - bullet.getRadius(),
                    bullet.getPosition().y - bullet.getRadius(),
                    bullet.getRadius() * 2,
                    bullet.getRadius() * 2
            );
        }

        // fin de las transformaciones del mundo
        gc.restore();

        // ================== UI / MIRA (coordenadas de pantalla) ==================

        if (mouseInside) {
            double size = 12; // largo de las líneas de la mira

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);

            // línea horizontal
            gc.strokeLine(mouseX - size, mouseY, mouseX + size, mouseY);
            // línea vertical
            gc.strokeLine(mouseX, mouseY - size, mouseX, mouseY + size);

            // pequeño círculo en el centro
            gc.strokeOval(mouseX - 3, mouseY - 3, 6, 6);
        }
    }


    private void updateHud() {
        healthLabel.setText("Vida: " + player.getHealth());
        weaponLabel.setText("Arma: " + player.getCurrentWeapon());
        ammoLabel.setText("Munición: " + player.getAmmo());
    }


    // manejadores de teclado, conectados desde MainApp
    public void onKeyPressed(KeyCode code) {
        pressedKeys.add(code);
    }

    public void onKeyReleased(KeyCode code) {
        pressedKeys.remove(code);
    }

    private void updateEnemyBullets(double delta) {
        for (EnemyBullet bullet : enemyBullets) {
            bullet.update(delta, gameMap);

            if (!bullet.isDead() && bullet.collidesWith(player)) {
                bullet.markDead();
                player.takeDamage(bullet.getDamage());
                // pequeño cooldown para no comer 3 balas en el mismo frame si se alinean
                playerHitCooldown = 0.3;
                updateHud();
            }
        }

        enemyBullets.removeIf(EnemyBullet::isDead);

        if (player.isDead()) {
            timer.stop();
            mainApp.showMainMenu();
        }
    }


    @FXML
    private void handleBackToMenu() {
        if (timer != null) timer.stop();
        mainApp.showMainMenu();
    }
}
