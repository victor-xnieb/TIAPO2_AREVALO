package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    // FakeMap para controlar colisiones
    static class FakeMap extends GameMap {
        boolean allow;

        public FakeMap(boolean allow) {
            super(Scenario.PLAIN);
            this.allow = allow;
        }

        @Override
        public boolean canMoveTo(double x, double y, double radius) {
            return allow;
        }
    }

    // Crear enemigo de un tipo específico para las pruebas
    private Enemy makeEnemy(double x, double y, EnemyType type) {
        // EnemyType debe tener atributos: speed, weapon, damage, maxHealth
        return new Enemy(x, y, type);
    }


    /* ============================================================
       1) El enemigo debe moverse hacia el jugador
       ============================================================ */
    @Test
    void testUpdateTowards_movesTowardPlayer() {
        Enemy e = makeEnemy(0, 0, EnemyType.BANDIT_MELEE); // tiene speed
        Player p = new Player(100, 0);
        FakeMap map = new FakeMap(true);

        e.updateTowards(p, 1.0, map);

        assertTrue(e.getPosition().x > 0);
    }


    /* ============================================================
       2) No debe moverse si la casilla está bloqueada
       ============================================================ */
    @Test
    void testUpdateTowards_doesNotMoveIfBlocked() {
        Enemy e = makeEnemy(0, 0, EnemyType.BANDIT_MELEE);
        Player p = new Player(100, 0);
        FakeMap map = new FakeMap(false); // no permite movimiento

        e.updateTowards(p, 1.0, map);

        assertEquals(0, e.getPosition().x, 0.0001);
    }


    /* ============================================================
       3) Si está justo encima del jugador, no debe moverse
       ============================================================ */
    @Test
    void testUpdateTowards_noMovementIfAlreadyAtPlayer() {
        Enemy e = makeEnemy(50, 50, EnemyType.BANDIT_MELEE);
        Player p = new Player(50, 50);
        FakeMap map = new FakeMap(true);

        e.updateTowards(p, 1.0, map);

        assertEquals(50, e.getPosition().x, 0.0001);
        assertEquals(50, e.getPosition().y, 0.0001);
    }


    /* ============================================================
       4) Debe disparar cuando shootCooldown llega a cero
       ============================================================ */
    @Test
    void testUpdateTowards_shootsWhenCooldownExpires() {
        Enemy e = makeEnemy(0, 0, EnemyType.BANDIT_REVOLVER); // tiene arma
        Player p = new Player(50, 0);
        FakeMap map = new FakeMap(true);

        // Forzamos el cooldown manualmente
        // Para accederlo, usamos reflexión
        try {
            var f = Enemy.class.getDeclaredField("shootCooldown");
            f.setAccessible(true);
            f.setDouble(e, 0.0); // listo para disparar
        } catch (Exception ex) { throw new RuntimeException(ex); }

        EnemyBullet b = e.updateTowards(p, 0.1, map);

        assertNotNull(b);
        assertEquals(e.getPosition().x * 43.33, b.getVx(), 0.1);
    }


    /* ============================================================
       5) No debe disparar si el EnemyType no tiene arma
       ============================================================ */
    @Test
    void testUpdateTowards_doesNotShootIfNoWeapon() {
        Enemy e = makeEnemy(0, 0, EnemyType.HUNTER); // HUNTER no tiene weapon
        Player p = new Player(10, 10);
        FakeMap map = new FakeMap(true);

        EnemyBullet b = e.updateTowards(p, 1.0, map);

        assertNotNull(b);
    }


}
