package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BulletTest {

    // Fake map que permite controlar colisiones
    static class FakeMap extends GameMap {
        boolean allowMove;

        public FakeMap(boolean allowMove) {
            super(Scenario.PLAIN);
            this.allowMove = allowMove;
        }

        @Override
        public boolean canMoveTo(double x, double y, double radius) {
            return allowMove;
        }
    }

    /* ============================================================
       1) La bala debe moverse según vx, vy y delta
       ============================================================ */
    @Test
    void testUpdate_movesCorrectly() {
        Bullet b = new Bullet(0, 0, 100, 0, WeaponType.REVOLVER);
        FakeMap map = new FakeMap(true);

        b.update(1.0, map);  // 1 segundo

        assertTrue(b.position.x > 0);
        assertEquals(0, b.position.y, 0.001);
    }

    /* ============================================================
       2) La vida disminuye en delta
       ============================================================ */
    @Test
    void testUpdate_decreasesLife() {
        Bullet b = new Bullet(0, 0, 100, 0, WeaponType.REVOLVER);
        FakeMap map = new FakeMap(true);

        b.update(0.5, map); // medio segundo

        // Vida inicial es 2.0
        // Después de 0.5 debería ser 1.5 o menos
        assertFalse(b.isDead());
    }

    /* ============================================================
       3) Muere cuando la vida llega a 0
       ============================================================ */
    @Test
    void testUpdate_diesWhenLifeExpires() {
        Bullet b = new Bullet(0, 0, 10, 0, WeaponType.REVOLVER);
        FakeMap map = new FakeMap(true);

        // Actualiza dos segundos completos
        b.update(2.0, map);

        assertTrue(b.isDead());
    }

    /* ============================================================
       4) La bala muere si no puede moverse (colisión)
       ============================================================ */
    @Test
    void testUpdate_diesOnCollision() {
        Bullet b = new Bullet(0, 0, 100, 0, WeaponType.REVOLVER);
        FakeMap map = new FakeMap(false);  // no se puede mover → pared

        b.update(0.1, map);

        assertTrue(b.isDead());
    }

    /* ============================================================
       5) Si ya está muerta, update no cambia nada
       ============================================================ */
    @Test
    void testUpdate_doesNothingWhenDead() {
        Bullet b = new Bullet(0, 0, 100, 0, WeaponType.REVOLVER);
        FakeMap map = new FakeMap(true);

        b.markDead();
        b.update(1.0, map);

        // No debe revivir ni moverse
        assertTrue(b.isDead());
    }


}
