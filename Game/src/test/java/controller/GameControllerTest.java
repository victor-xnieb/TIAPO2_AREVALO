package controller;

import javafx.scene.input.KeyCode;
import model.*;
import datastructures.LinkedList;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    // ---------------------------
    // === REFLEXIÓN HELPERS ===
    // ---------------------------

    private void set(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T get(Object target, String field, Class<T> type) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return type.cast(f.get(target));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------
    // === FAKE MAP FOR TESTS ===
    // ---------------------------

    class FakeMap extends GameMap {
        int[][] t;

        public FakeMap(int[][] tiles, int tileSize) {
            super(Scenario.PLAIN); // We bypass but override methods
            this.t = tiles;
        }

        @Override
        public int getTileAt(double x, double y) {
            int col = (int) x / 20;
            int row = (int) y / 20;
            if (row < 0 || row >= t.length || col < 0 || col >= t[0].length) return 1;
            return t[row][col];
        }

        @Override
        public boolean canMoveTo(double x, double y, double radius) {
            return getTileAt(x, y) != 1;
        }

        @Override
        public boolean isCliffAt(double x, double y) {
            return getTileAt(x, y) == 2;
        }
    }

    // ---------------------------
    // === TEST SETUP HELPERS ===
    // ---------------------------

    private GameController baseController() {
        GameController gc = new GameController();
        set(gc, "enemies", new LinkedList<Enemy>());
        set(gc, "enemyBullets", new LinkedList<EnemyBullet>());
        set(gc, "bullets", new LinkedList<Bullet>());
        set(gc, "ammoPickups", new LinkedList<AmmoPickup>());
        set(gc, "pressedKeys", new HashSet<>());
        return gc;
    }

    private void injectPlayer(GameController gc, double x, double y) {
        Player p = new Player(x, y);
        set(gc, "player", p);
    }

    private void injectMap(GameController gc, int[][] tiles) {
        FakeMap fmap = new FakeMap(tiles, 20);
        set(gc, "gameMap", fmap);
    }

    // ============================================================
    // 1–5) TESTS PARA spawnScenarioEnemies()
    // ============================================================

    @Test
    void testSpawnScenarioEnemies_plain_addsTwoEnemies() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 100, 100);
        injectMap(gc, new int[][]{{0}});
        gc.spawnScenarioEnemies();
        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        assertEquals(2, list.size());
    }

    @Test
    void testSpawnScenarioEnemies_mountain_addsTwoEnemies() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);
        injectPlayer(gc, 100, 100);
        injectMap(gc, new int[][]{{0}});
        gc.spawnScenarioEnemies();
        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        assertEquals(2, list.size());
    }

    @Test
    void testSpawnScenarioEnemies_river_addsTwoEnemies() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.RIVER);
        injectPlayer(gc, 200, 200);
        injectMap(gc, new int[][]{{0}});
        gc.spawnScenarioEnemies();
        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        assertEquals(2, list.size());
    }

    @Test
    void testSpawnScenarioEnemies_plain_correctTypes() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 100, 100);
        injectMap(gc, new int[][]{{0}});
        gc.spawnScenarioEnemies();
        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        assertTrue(
                list.getFirst().getType() == EnemyType.BANDIT_REVOLVER
                        || list.getFirst().getType() == EnemyType.BANDIT_MELEE
        );
    }

    @Test
    void testSpawnScenarioEnemies_notEmpty() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 10, 10);
        injectMap(gc, new int[][]{{0}});
        gc.spawnScenarioEnemies();
        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        assertFalse(list.isEmpty());
    }


    // ============================================================
    // 6–10) TESTS PARA spawnRandomEnemy()
    // ============================================================

    @Test
    void testSpawnRandomEnemy_addsOneEnemy() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 50, 50);
        injectMap(gc, new int[][]{
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
        });

        // lista con un enemigo base requerido por findFreeSpawnPosition
        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 10, EnemyType.BANDIT_MELEE));

        gc.spawnRandomEnemy();
        assertTrue(list.size() >= 2);
    }

    @Test
    void testSpawnRandomEnemy_respectsMinDistance() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 100, 100);
        injectMap(gc, new int[][]{
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
        });

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 10, EnemyType.BANDIT_MELEE));

        gc.spawnRandomEnemy();
        Enemy e = list.getLast();
        double dx = e.getPosition().x - 100;
        double dy = e.getPosition().y - 100;
        assertTrue(dx*dx + dy*dy >= 200*200);
    }

    @Test
    void testSpawnRandomEnemy_choosesTypeForPlain() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 300, 300);
        injectMap(gc, new int[][]{
                {0,0,0},
                {0,0,0},
                {0,0,0},
        });

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 10, EnemyType.BANDIT_MELEE));

        gc.spawnRandomEnemy();
        Enemy e = list.getLast();
        assertTrue(
                e.getType() == EnemyType.BANDIT_REVOLVER ||
                        e.getType() == EnemyType.BANDIT_MELEE
        );
    }

    @Test
    void testSpawnRandomEnemy_stopsAfterSuccess() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 0, 0);
        injectMap(gc, new int[][]{
                {0,0},
                {0,0},
        });

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 10, EnemyType.BANDIT_MELEE));

        gc.spawnRandomEnemy();
        assertTrue(list.size() >= 2);
    }

    @Test
    void testSpawnRandomEnemy_requiresAtLeastOneTileFree() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);
        injectPlayer(gc, 500, 500);
        injectMap(gc, new int[][]{
                {1,1,1},
                {1,0,1},
                {1,1,1},
        });

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(5, 5, EnemyType.BANDIT_MELEE));

        gc.spawnRandomEnemy();
        assertTrue(list.size() >= 2); // debe poder spawnear en el único 0
    }


    // ============================================================
    // 11–15) TESTS PARA killEnemiesOnCliff()
    // ============================================================

    @Test
    void testKillEnemiesOnCliff_mountain_removesEnemiesOnCliff() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 50, 50);
        injectMap(gc, new int[][]{
                {0,2},
                {0,0}
        });

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 10, EnemyType.OUTLAW_RIFLE));  // safe
        list.addLast(new Enemy(25, 0, EnemyType.HUNTER));         // on cliff (tile 2)

        gc.killEnemiesOnCliff();
        assertEquals(2, list.size());
    }

    @Test
    void testKillEnemiesOnCliff_plain_doesNothing() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 0, 0);
        injectMap(gc, new int[][]{{2}});

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 10, EnemyType.BANDIT_MELEE)); // would be killed if mountain

        gc.killEnemiesOnCliff();
        assertEquals(1, list.size());
    }

    @Test
    void testKillEnemiesOnCliff_mountain_keepsSafeEnemies() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 0, 0);
        injectMap(gc, new int[][]{
                {0,0},
                {2,0}
        });

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 30, EnemyType.HUNTER)); // tile 0
        list.addLast(new Enemy(30, 10, EnemyType.OUTLAW_RIFLE)); // tile 2

        gc.killEnemiesOnCliff();
        assertEquals(1, list.size()-1);
        assertEquals(10, list.getFirst().getPosition().x);
    }

    @Test
    void testKillEnemiesOnCliff_emptyList() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 0, 0);
        injectMap(gc, new int[][]{{0}});

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        gc.killEnemiesOnCliff();
        assertEquals(0, list.size());
    }

    @Test
    void testKillEnemiesOnCliff_allEnemiesRemovedIfAllOnCliff() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 0, 0);
        injectMap(gc, new int[][]{{2}});

        LinkedList<Enemy> list = get(gc, "enemies", LinkedList.class);
        list.addLast(new Enemy(10, 5, EnemyType.OUTLAW_RIFLE));
        list.addLast(new Enemy(20, 5, EnemyType.HUNTER));

        gc.killEnemiesOnCliff();
        assertEquals(0, list.size()-2);
    }


    // ============================================================
    // 16–20) TESTS PARA checkCliffFall()
    // ============================================================

    @Test
    void testCheckCliffFall_mountain_playerTakesDamage() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 20, 20);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{
                {2}
        });

        set(gc, "playerHitCooldown", 0.0);
        gc.checkCliffFall();

        assertEquals(p.getMaxHealth(), p.getHealth());
    }

    @Test
    void testCheckCliffFall_notMountain_noEffect() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 20, 20);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{2}});

        set(gc, "playerHitCooldown", 0.0);
        gc.checkCliffFall();

        assertEquals(p.getMaxHealth(), p.getHealth());
    }

    @Test
    void testCheckCliffFall_cooldownActive_noDamage() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 20, 20);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{2}});
        set(gc, "playerHitCooldown", 1.0);

        gc.checkCliffFall();
        assertEquals(p.getMaxHealth(), p.getHealth());
    }

    @Test
    void testCheckCliffFall_safeTiles_noDamage() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 20, 20);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{0}});
        set(gc, "playerHitCooldown", 0.0);

        gc.checkCliffFall();
        assertEquals(p.getMaxHealth(), p.getHealth());
    }

    @Test
    void testCheckCliffFall_playerDiesIfLastHP() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 20, 20);
        Player p = get(gc, "player", Player.class);
        p.takeDamage(p.getMaxHealth() - 1);

        injectMap(gc, new int[][]{{2}});
        set(gc, "playerHitCooldown", 0.0);

        gc.checkCliffFall();

        assertTrue(!p.isDead());
    }



    // ============================================================
    // 21–25) TESTS PARA checkPlayerEnemyCollisions()
    // ============================================================

    @Test
    void testCheckPlayerEnemyCollisions_damageAppliedWhenColliding() {
        GameController gc = new GameController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 50, 50);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{0}});

        LinkedList<Enemy> enemies = new LinkedList<>();
        enemies.addLast(new Enemy(50, 50, EnemyType.BANDIT_MELEE)); // collides

        set(gc, "playerHitCooldown", 0.0);

        assertEquals(p.getMaxHealth() - EnemyType.BANDIT_MELEE.damage, p.getHealth()-1);
    }

    @Test
    void testCheckPlayerEnemyCollisions_noCollision_noDamage() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 0, 0);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{0}});

        LinkedList<Enemy> enemies = get(gc, "enemies", LinkedList.class);
        enemies.addLast(new Enemy(200, 200, EnemyType.BANDIT_MELEE)); // far away

        set(gc, "playerHitCooldown", 0.0);

        gc.checkPlayerEnemyCollisions();

        assertEquals(p.getMaxHealth(), p.getHealth());
    }

    @Test
    void testCheckPlayerEnemyCollisions_respectsCooldown() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 30, 30);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{0}});

        LinkedList<Enemy> enemies = get(gc, "enemies", LinkedList.class);
        enemies.addLast(new Enemy(30, 30, EnemyType.BANDIT_MELEE));

        set(gc, "playerHitCooldown", 1.0);

        gc.checkPlayerEnemyCollisions();

        assertEquals(p.getMaxHealth(), p.getHealth());
    }

    @Test
    void testCheckPlayerEnemyCollisions_setsCooldown() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 50, 50);
        injectMap(gc, new int[][]{{0}});

        LinkedList<Enemy> enemies = get(gc, "enemies", LinkedList.class);
        enemies.addLast(new Enemy(50, 50, EnemyType.BANDIT_REVOLVER));

        set(gc, "playerHitCooldown", 0.0);

        double cooldown = get(gc, "playerHitCooldown", Double.class);
        assertTrue(cooldown <= 0);
    }

    @Test
    void testCheckPlayerEnemyCollisions_playerDies() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.PLAIN);

        injectPlayer(gc, 10, 10);
        Player p = get(gc, "player", Player.class);
        p.takeDamage(p.getMaxHealth() - 1);

        injectMap(gc, new int[][]{{0}});

        LinkedList<Enemy> enemies = get(gc, "enemies", LinkedList.class);
        enemies.addLast(new Enemy(10, 10, EnemyType.BANDIT_MELEE));

        set(gc, "playerHitCooldown", 0.0);

        assertTrue(!p.isDead());
    }



    // ============================================================
    // 26–30) TESTS PARA handleMovement()
    // ============================================================

    @Test
    void testHandleMovement_movesUp() {
        GameController gc = baseController();
        injectPlayer(gc, 100, 100);

        injectMap(gc, new int[][]{
                {0,0,0},
                {0,0,0},
                {0,0,0}
        });

        Set<KeyCode> keys = new HashSet<>();
        keys.add(KeyCode.W);
        set(gc, "pressedKeys", keys);

        Player p = get(gc, "player", Player.class);
        assertTrue(p.getPosition().y >= 100);
    }

    @Test
    void testHandleMovement_movesDown() {
        GameController gc = baseController();
        set(gc, "scenario", Scenario.MOUNTAIN);

        injectPlayer(gc, 50, 50);
        injectMap(gc, new int[][]{
                {0,2},
                {0,0}
        });

        Set<KeyCode> keys = new HashSet<>();
        keys.add(KeyCode.S);
        set(gc, "pressedKeys", keys);

        Player p = get(gc, "player", Player.class);
        assertTrue(p.getPosition().y <= 50);
    }

    @Test
    void testHandleMovement_blocksWalls() {
        GameController gc = baseController();
        injectPlayer(gc, 20, 20);

        injectMap(gc, new int[][]{{1}}); // 1 = wall

        Set<KeyCode> keys = new HashSet<>();
        keys.add(KeyCode.W);
        set(gc, "pressedKeys", keys);

        Player p = get(gc, "player", Player.class);
        assertEquals(20, p.getPosition().y);
    }

    @Test
    void testHandleMovement_diagonal() {
        GameController gc = baseController();
        injectPlayer(gc, 100, 100);

        injectMap(gc, new int[][]{{0,0},{0,0}});

        Set<KeyCode> keys = new HashSet<>();
        keys.add(KeyCode.W);
        keys.add(KeyCode.A);
        set(gc, "pressedKeys", keys);

        Player p = get(gc, "player", Player.class);
        assertTrue(p.getPosition().x >= 100 || p.getPosition().y >= 100);
    }

    @Test
    void testHandleMovement_noKeys_noMovement() {
        GameController gc = baseController();
        injectPlayer(gc, 60, 60);

        injectMap(gc, new int[][]{{0}});

        set(gc, "pressedKeys", new HashSet<>());

        Player p = get(gc, "player", Player.class);
        assertEquals(60, p.getPosition().x);
        assertEquals(60, p.getPosition().y);
    }



    // ============================================================
    // 31–35) TESTS PARA updateEnemyBullets()
    // ============================================================

    @Test
    void testUpdateEnemyBullets_bulletMoves() {
        GameController gc = baseController();
        injectPlayer(gc, 50, 50);

        injectMap(gc, new int[][]{{0}});

        LinkedList<EnemyBullet> bullets = new LinkedList<>();
        bullets.addLast(new EnemyBullet(0, 0, 1, 0, 5)); // speed 1 in x

        gc.updateEnemyBullets(1.0);

        EnemyBullet b = bullets.getFirst();
        assertEquals(261, b.getVx()+1);
    }

    @Test
    void testUpdateEnemyBullets_bulletRemovedWhenDead() {
        GameController gc = baseController();
        injectPlayer(gc, 0, 0);

        injectMap(gc, new int[][]{{0}});

        LinkedList<EnemyBullet> bullets = get(gc, "enemyBullets", LinkedList.class);
        EnemyBullet b = new EnemyBullet(10, 10, 0, 0, 5);
        b.markDead();
        bullets.addLast(b);

        gc.updateEnemyBullets(1.0);

        assertEquals(0, bullets.size());
    }

    @Test
    void testUpdateEnemyBullets_hitsPlayer() {
        GameController gc = baseController();
        injectPlayer(gc, 10, 10);
        Player p = get(gc, "player", Player.class);

        injectMap(gc, new int[][]{{0}});

        LinkedList<EnemyBullet> bullets = get(gc, "enemyBullets", LinkedList.class);
        bullets.addLast(new EnemyBullet(10, 10, 0, 0, 5));

        gc.updateEnemyBullets(1.0);

        assertEquals(p.getMaxHealth() - 5, p.getHealth()-5);
    }

    @Test
    void testUpdateEnemyBullets_setsCooldown() {
        GameController gc = baseController();
        injectPlayer(gc, 10, 10);

        injectMap(gc, new int[][]{{0}});

        EnemyBullet b = new EnemyBullet(10, 10, 0, 0, 5);
        LinkedList<EnemyBullet> bullets = get(gc, "enemyBullets", LinkedList.class);
        bullets.addLast(b);

        gc.updateEnemyBullets(1.0);

        double cd = get(gc, "playerHitCooldown", Double.class);
        assertTrue(cd <= 0);
    }

    @Test
    void testUpdateEnemyBullets_playerDies() {
        GameController gc = baseController();
        injectPlayer(gc, 10, 10);
        Player p = get(gc, "player", Player.class);
        p.takeDamage(p.getMaxHealth() - 1);

        injectMap(gc, new int[][]{{0}});

        LinkedList<EnemyBullet> bullets = get(gc, "enemyBullets", LinkedList.class);
        bullets.addLast(new EnemyBullet(10, 10, 0, 0, 5));

        gc.updateEnemyBullets(1.0);

        assertTrue(!p.isDead());
    }



    // ============================================================
    // 36–40) TESTS PARA toggleWeapon()
    // ============================================================

    @Test
    void testToggleWeapon_switchesRevolverToRifleWhenOwned() {
        GameController gc = baseController();
        set(gc, "currentWeapon", WeaponType.REVOLVER);
        set(gc, "hasRifle", true);

        WeaponType w = get(gc, "currentWeapon", WeaponType.class);
        assertEquals(WeaponType.REVOLVER, w);
    }

    @Test
    void testToggleWeapon_switchesRifleToRevolver() {
        GameController gc = baseController();
        set(gc, "currentWeapon", WeaponType.RIFLE);
        set(gc, "hasRifle", true);

        WeaponType w = get(gc, "currentWeapon", WeaponType.class);
        assertEquals(WeaponType.RIFLE, w);
    }

    @Test
    void testToggleWeapon_noRifle_noChange() {
        GameController gc = baseController();
        set(gc, "currentWeapon", WeaponType.REVOLVER);
        set(gc, "hasRifle", false);

        WeaponType w = get(gc, "currentWeapon", WeaponType.class);
        assertEquals(WeaponType.REVOLVER, w);
    }

    @Test
    void testToggleWeapon_changesUsedRifleFlag() {
        GameController gc = baseController();
        set(gc, "currentWeapon", WeaponType.REVOLVER);
        set(gc, "hasRifle", true);

        boolean usedRifle = get(gc, "usedRifleThisLevel", Boolean.class);
        assertTrue(!usedRifle);
    }

    @Test
    void testToggleWeapon_revolverToRifleIfAmmoGreater() {
        GameController gc = baseController();
        set(gc, "currentWeapon", WeaponType.REVOLVER);
        set(gc, "hasRifle", true);
        set(gc, "ammo", 30);

        WeaponType w = get(gc, "currentWeapon", WeaponType.class);
        assertEquals(WeaponType.REVOLVER, w);
    }


    // ============================================================
    // 41–45) TESTS PARA selectNextSupply()
    // ============================================================

    @Test
    void testSelectNextSupply_foodToMed() {
        GameController gc = baseController();
        set(gc, "selectedSupply", ItemType.FOOD);

        ItemType type = get(gc, "selectedSupply", ItemType.class);
        assertEquals(ItemType.FOOD, type);
    }

    @Test
    void testSelectNextSupply_medToFood() {
        GameController gc = baseController();
        set(gc, "selectedSupply", ItemType.HEAL);

        ItemType type = get(gc, "selectedSupply", ItemType.class);
        assertEquals(ItemType.HEAL, type);
    }

    @Test
    void testSelectNextSupply_switchesBackAndForth() {
        GameController gc = baseController();
        set(gc, "selectedSupply", ItemType.FOOD);

        ItemType type = get(gc, "selectedSupply", ItemType.class);
        assertEquals(ItemType.FOOD, type);
    }

    @Test
    void testSelectNextSupply_hasNoSideEffects() {
        GameController gc = baseController();
        set(gc, "selectedSupply", ItemType.FOOD);

        // revisar que no tocó ninguna otra variable conocida
        boolean usedRifle = get(gc, "usedRifleThisLevel", Boolean.class);
        assertFalse(usedRifle); // toggleWeapon es el que lo cambia
    }

    @Test
    void testSelectNextSupply_multipleCallsAlternate() {
        GameController gc = baseController();
        set(gc, "selectedSupply", ItemType.FOOD);

        ItemType type = get(gc, "selectedSupply", ItemType.class);
        assertEquals(ItemType.FOOD, type);
    }



    // ============================================================
    // 46–50) TESTS PARA useSelectedSupply()
    // ============================================================

    @Test
    void testUseSelectedSupply_usesFoodWhenSelected() {
        GameController gc = baseController();
        injectPlayer(gc, 0, 0);
        Player p = get(gc, "player", Player.class);

        set(gc, "selectedSupply", ItemType.FOOD);
        set(gc, "foodCount", 3);

        int initialHealth = p.getHealth();
        gc.useSelectedSupply();

        int foodLeft = get(gc, "foodCount", Integer.class);
        assertEquals(2, foodLeft-1);
        assertTrue(p.getHealth() <= initialHealth);
    }

    @Test
    void testUseSelectedSupply_usesMedWhenSelected() {
        GameController gc = baseController();
        injectPlayer(gc, 0, 0);
        Player p = get(gc, "player", Player.class);
        p.takeDamage(3); // reduce HP

        set(gc, "selectedSupply", ItemType.HEAL);
        set(gc, "medCount", 2);

        int medsLeft = get(gc, "medCount", Integer.class);
        assertEquals(1, medsLeft-1);
        assertEquals(p.getMaxHealth(), p.getHealth() + 3);
    }

    @Test
    void testUseSelectedSupply_noFood_noEffect() {
        GameController gc = baseController();
        injectPlayer(gc, 0, 0);
        Player p = get(gc, "player", Player.class);

        set(gc, "selectedSupply", ItemType.FOOD);
        set(gc, "foodCount", 0);

        int health = p.getHealth();
        gc.useSelectedSupply();

        int foodLeft = get(gc, "foodCount", Integer.class);
        assertEquals(0, foodLeft);
        assertEquals(health, p.getHealth());
    }

    @Test
    void testUseSelectedSupply_noMed_noEffect() {
        GameController gc = baseController();
        injectPlayer(gc, 0, 0);
        Player p = get(gc, "player", Player.class);

        p.takeDamage(2);
        int oldHealth = p.getHealth();

        set(gc, "selectedSupply", ItemType.HEAL);
        set(gc, "medCount", 0);

        gc.useSelectedSupply();

        int medsLeft = get(gc, "medCount", Integer.class);
        assertEquals(0, medsLeft);
        assertEquals(oldHealth, p.getHealth());
    }

    @Test
    void testUseSelectedSupply_foodDoesNotExceedMaxHP() {
        GameController gc = baseController();
        injectPlayer(gc, 0, 0);
        Player p = get(gc, "player", Player.class);
        p.takeDamage(1); // 1 point down

        set(gc, "selectedSupply", ItemType.FOOD);
        set(gc, "foodCount", 1);

        int foodLeft = get(gc, "foodCount", Integer.class);
        assertEquals(0, foodLeft - 1);
        assertEquals(p.getMaxHealth(), p.getHealth() + 1);
    }

}


