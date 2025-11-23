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
    // índice 0: IDLE
    // índice 1: WALK
    // índice 2: SHOOT_REVOLVER
    // índice 3: SHOOT_RIFLE
    private Image[][] playerAnimations;

    private static final int ANIM_IDLE           = 0;
    private static final int ANIM_WALK           = 1;
    private static final int ANIM_SHOOT_REVOLVER = 2;
    private static final int ANIM_SHOOT_RIFLE    = 3;

    private static final long PLAYER_ANIM_FRAME_NS  = 150_000_000L; // ~0.15s
    private static final long PLAYER_SHOOT_ANIM_NS  = 150_000_000L;

    private boolean playerMoving   = false;
    private boolean playerShooting = false;
    private long lastShotTimeNanos;


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



    // --- armas del jugador ---
    private WeaponType currentWeapon = WeaponType.REVOLVER;
    // por ahora lo dejamos true para que puedas probar el cambio de sprites;
    // cuando pongamos el rifle en el suelo lo inicializamos en false
    private boolean hasRifle = false;

    // munición total actual
    private int ammo;

    // munición máxima (para el HUD / pickups)
    private int maxAmmo = 30;

    // pickups de armas
    private LinkedList<WeaponPickup> weaponPickups;

    // imagen del rifle en el suelo
    private Image riflePickupImage;



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

                // límites
                if (zoom < 0.4) zoom = 0.4;
                if (zoom > 2.5) zoom = 2.5;

                // muy importante: volver a limitar la cámara
                clampCameraToWorld();
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
        // 1) Fondo
        background = new Image(getClass().getResourceAsStream(scenario.getImagePath()));

        // 2) Mapa
        gameMap = new GameMap(scenario);

        // 3) Frames de enemigos
        enemyFrames = new Image[] {
                new Image(getClass().getResourceAsStream("/images/enemigo1/enemigo.png")),
                new Image(getClass().getResourceAsStream("/images/enemigo1/enemigo2.png")),
                new Image(getClass().getResourceAsStream("/images/enemigo1/enemigo3.png")),
                new Image(getClass().getResourceAsStream("/images/enemigo1/enemigo4.png"))
        };

        playerAnimations = new Image[4][];

        // IDLE: un solo frame
        playerAnimations[ANIM_IDLE] = new Image[] {
                new Image(getClass().getResourceAsStream("/images/jugador/jugador3.png"))
        };

        // WALK: dos frames que alternan
        playerAnimations[ANIM_WALK] = new Image[] {
                new Image(getClass().getResourceAsStream("/images/jugador/jugador1.png")),
                new Image(getClass().getResourceAsStream("/images/jugador/jugador2.png"))
        };

        // SHOOT_REVOLVER: un frame
        playerAnimations[ANIM_SHOOT_REVOLVER] = new Image[] {
                new Image(getClass().getResourceAsStream("/images/jugador/jugador_revolver.png"))
        };

        // SHOOT_RIFLE: un frame
        playerAnimations[ANIM_SHOOT_RIFLE] = new Image[] {
                new Image(getClass().getResourceAsStream("/images/jugador/jugador_rifle.png"))
        };

        // 4) Jugador y listas
        player = new Player(gameMap.getWidth() / 2.0, gameMap.getHeight() / 2.0);
        enemies = new LinkedList<>();
        bullets = new LinkedList<>();
        ammoPickups = new LinkedList<>();
        enemyBullets = new LinkedList<>();


        currentWeapon = WeaponType.REVOLVER;
        hasRifle = false;   // 🚨 ahora empieza solo con revólver

        ammo = 20;          // balas iniciales (ajusta si quieres)
        maxAmmo = 30;

        // --- pickups de arma ---
        weaponPickups = new LinkedList<>();

        // imagen del rifle en el suelo
        riflePickupImage = new Image(
                getClass().getResourceAsStream("/images/armas/rifle_piso.png")
        );

        // 5) Cámara + zoom inicial
        cameraX = player.getPosition().x;
        cameraY = player.getPosition().y;

        double w = gameCanvas.getWidth();
        double h = gameCanvas.getHeight();

        double zx = w / gameMap.getWidth();
        double zy = h / gameMap.getHeight();
        zoom = Math.min(zx, zy);
        if (zoom > 1.0) zoom = 1.0;
        if (zoom < 0.4) zoom = 0.4;

        clampCameraToWorld();

        // 6) Enemigos iniciales, HUD y loop
        spawnScenarioEnemies();
        spawnScenarioWeapons();
        updateHud();
        startLoop();   // 👈 SIEMPRE al FINAL
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
        checkCliffFall();
        updateEnemies(delta);
        killEnemiesOnCliff();
        updateBullets(delta);
        updateEnemyBullets(delta);   // lo añadimos luego
        updateAmmoPickups();
        updateWeaponPickups();
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

        // eje X
        if (worldW <= viewW) {
            cameraX = worldW / 2.0;
        } else {
            cameraX = Math.max(halfW, Math.min(worldW - halfW, cameraX));
        }

        // eje Y
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

        boolean moved = false;

        if (dx != 0 || dy != 0) {
            double r = player.getRadius();

            double newX = player.getPosition().x + dx;
            double newY = player.getPosition().y + dy;

            if (gameMap.canMoveTo(newX, player.getPosition().y, r)) {
                player.move(dx, 0);
                moved = true;
            }
            if (gameMap.canMoveTo(player.getPosition().x, newY, r)) {
                player.move(0, dy);
                moved = true;
            }
        }

        playerMoving = moved;

        // 🔁 antes usábamos Q, ahora C
        if (pressedKeys.contains(KeyCode.C)) {
            toggleWeapon();
        }

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
        // jugador con sprite
        Image sprite = choosePlayerSprite();
        double size = player.getRadius() * 4; // ajusta hasta que se vea del tamaño que te guste

        double px = player.getPosition().x - size / 2;
        double py = player.getPosition().y - size / 2;

        gc.drawImage(sprite, px, py, size, size);


        // ---- enemigos (animados con frames) ----
        // ---- enemigos (animados con frames) ----
        Image enemyFrame = null;
        if (enemyFrames != null && enemyFrames.length > 0) {
            long now = System.nanoTime();
            int frameIndex = (int) ((now / 150_000_000L) % enemyFrames.length);
            enemyFrame = enemyFrames[frameIndex];
        }

        for (Enemy enemy : enemies) {
            double x = enemy.getPosition().x;
            double y = enemy.getPosition().y;
            double size2 = enemy.getRadius() * 4;

            if (enemyFrame != null) {
                gc.drawImage(enemyFrame,
                        x - size2 / 2,
                        y - size2 / 2,
                        size2,
                        size2);
            } else {
                // fallback por si algo falla al cargar las imágenes
                gc.setFill(Color.RED);
                gc.fillOval(
                        x - enemy.getRadius(),
                        y - enemy.getRadius(),
                        enemy.getRadius() * 2,
                        enemy.getRadius() * 2
                );
            }
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

        // pickups de armas (rifle en el suelo)
        if (riflePickupImage != null && weaponPickups != null) {
            for (WeaponPickup wp : weaponPickups) {
                double size4 = wp.getRadius() * 2;

                gc.drawImage(
                        riflePickupImage,
                        wp.getPosition().x - size4 / 2,
                        wp.getPosition().y - size4 / 2,
                        size4,
                        size4
                );
            }
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
            double size3 = 12; // largo de las líneas de la mira

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);

            // línea horizontal
            gc.strokeLine(mouseX - size3, mouseY, mouseX + size3, mouseY);
            // línea vertical
            gc.strokeLine(mouseX, mouseY - size3, mouseX, mouseY + size3);

            // pequeño círculo en el centro
            gc.strokeOval(mouseX - 3, mouseY - 3, 6, 6);
        }
    }


    private void updateHud() {
        healthLabel.setText("Vida: " + player.getHealth());
        weaponLabel.setText("Arma: " + currentWeapon);
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


    private void killEnemiesOnCliff() {
        // solo tiene sentido en el escenario de montañas
        if (scenario != Scenario.MOUNTAIN) {
            return;
        }

        datastructures.LinkedList<Enemy> remaining = new datastructures.LinkedList<>();

        for (Enemy enemy : enemies) {
            double ex = enemy.getPosition().x;
            double ey = enemy.getPosition().y;

            // si NO está sobre un 2, lo conservamos
            if (!gameMap.isCliffAt(ex, ey)) {
                remaining.addLast(enemy);
            }
            // si sí está pisando un 2, simplemente NO lo añadimos -> desaparece
        }

        enemies = remaining;
    }

    private void checkCliffFall() {
        // Solo hay acantilado en Montañas
        if (scenario != Scenario.MOUNTAIN) {
            return;
        }

        // cooldown para no perder vida en bucle
        if (playerHitCooldown > 0) {
            return;
        }

        double x = player.getPosition().x;
        double y = player.getPosition().y;
        double r = player.getRadius() * 0.7; // un poco alrededor del jugador

        boolean onCliff =
                gameMap.isCliffAt(x, y) ||
                        gameMap.isCliffAt(x - r, y) ||
                        gameMap.isCliffAt(x + r, y) ||
                        gameMap.isCliffAt(x, y + r);

        if (onCliff) {
            player.takeDamage(1);
            updateHud();

            // ------------ RESPawn en un punto concreto del mapa ------------

            // coordenadas de la celda segura en la matriz (0 = primera fila/columna)
            int safeCol = 141 /* AQUÍ pones el índice de columna */;
            int safeRow = 88 /* AQUÍ pones el índice de fila    */;

            double safeX = (safeCol + 0.5) * gameMap.getTileSize();
            double safeY = (safeRow + 0.5) * gameMap.getTileSize();

            player.getPosition().x = safeX;
            player.getPosition().y = safeY;

            //----------------------------------------------------------------

            playerHitCooldown = 1.0; // para que no caiga en bucle

            if (player.isDead()) {
                timer.stop();
                mainApp.showMainMenu();
            }
        }
    }


    private Image choosePlayerSprite() {
        long now = System.nanoTime();

        // 1) Si está disparando, mostrar animación de disparo según el arma
        if (playerShooting && now - lastShotTimeNanos < PLAYER_SHOOT_ANIM_NS) {
            int animIndex = (currentWeapon == WeaponType.RIFLE)
                    ? ANIM_SHOOT_RIFLE
                    : ANIM_SHOOT_REVOLVER;

            Image[] frames = playerAnimations[animIndex];
            return frames[0]; // por ahora un solo frame
        }

        // Si ya pasó el tiempo del disparo, apagamos el flag
        if (playerShooting && now - lastShotTimeNanos >= PLAYER_SHOOT_ANIM_NS) {
            playerShooting = false;
        }

        // 2) Si se está moviendo, usamos la animación de caminar
        if (playerMoving) {
            Image[] walkFrames = playerAnimations[ANIM_WALK];
            int frame = (int) ((now / PLAYER_ANIM_FRAME_NS) % walkFrames.length);
            return walkFrames[frame];
        }

        // 3) Si está quieto, animación idle
        return playerAnimations[ANIM_IDLE][0];
    }




    private void toggleWeapon() {
        if (!hasRifle) {
            // todavía no ha recogido el rifle → no dejamos cambiar
            return;
        }

        if (currentWeapon == WeaponType.REVOLVER) {
            currentWeapon = WeaponType.RIFLE;
        } else {
            currentWeapon = WeaponType.REVOLVER;
        }
        updateHud();
    }



    private void shootAt(double targetX, double targetY) {
        if (ammo <= 0) {
            return;
        }

        double px = player.getPosition().x;
        double py = player.getPosition().y;

        // dirección hacia el mouse (puedes normalizar dentro del Bullet)
        double dx = targetX - px;
        double dy = targetY - py;

        // CREA AQUÍ TU BALA
        // Ajusta el constructor a tu clase Bullet.
        // Ejemplo genérico:
        Bullet bullet = new Bullet(px, py, dx, dy);
        bullets.addLast(bullet);

        ammo--;
        updateHud();

        // activar animación de disparo
        playerShooting = true;
        lastShotTimeNanos = System.nanoTime();
    }


    private void spawnScenarioWeapons() {
        weaponPickups.clear();

        Integer col = scenario.getRifleCol();
        Integer row = scenario.getRifleRow();

        // este escenario no tiene rifle en el suelo
        if (col == null || row == null) {
            return;
        }

        int tileSize = gameMap.getTileSize();

        double x = (col + 0.5) * tileSize;
        double y = (row + 0.5) * tileSize;

        weaponPickups.addLast(
                new WeaponPickup(x, y, 40d, WeaponType.RIFLE)
        );
    }




    private void updateWeaponPickups() {
        if (weaponPickups == null || weaponPickups.isEmpty()) return;

        datastructures.LinkedList<WeaponPickup> remaining = new datastructures.LinkedList<>();

        double px = player.getPosition().x;
        double py = player.getPosition().y;
        double pr = player.getRadius();

        for (WeaponPickup pickup : weaponPickups) {
            double dx = pickup.getPosition().x - px;
            double dy = pickup.getPosition().y - py;
            double distSq = dx * dx + dy * dy;
            double sumR   = pickup.getRadius() + pr;

            if (distSq <= sumR * sumR) {
                // lo recogió
                if (pickup.getWeaponType() == WeaponType.RIFLE) {
                    hasRifle = true;
                    currentWeapon = WeaponType.RIFLE; // si quieres que se equipe de una
                    updateHud();
                }
            } else {
                remaining.addLast(pickup);
            }
        }

        weaponPickups = remaining;
    }


    @FXML
    private void handleBackToMenu() {
        if (timer != null) timer.stop();
        mainApp.showMainMenu();
    }
}
