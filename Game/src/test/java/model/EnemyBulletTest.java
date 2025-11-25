package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnemyBulletTest {

    // FakeMap para controlar colisiones
    static class FakeMap extends GameMap {
        boolean pass;

        public FakeMap(boolean pass) {
            super(Scenario.PLAIN);
            this.pass = pass;
        }

        @Override
        public boolean canMoveTo(double x, double y, double radius) {
            return pass;
        }
    }

    /* ============================================================
       1) La bala debe moverse según vx, vy y delta
       ============================================================ */
    @Test
    void testUpdate_movesCorrectly() {
        EnemyBullet b = new EnemyBullet(0, 0, 100, 0, 2);
        FakeMap map = new FakeMap(true);

        b.update(1.0, map);

        assertTrue(b.position.x > 0);
        assertEquals(0, b.position.y, 0.001);
    }

    /* ============================================================
       2) La bala muere si choca con una pared (map.canMoveTo = false)
       ============================================================ */
    @Test
    void testUpdate_diesOnCollision() {
        EnemyBullet b = new EnemyBullet(0, 0, 100, 0, 2);
        FakeMap map = new FakeMap(false); // no permite movimiento

        b.update(0.1, map);

        assertTrue(b.isDead());
    }

    /* ============================================================
       3) La vida disminuye y muere cuando llega a cero
       ============================================================ */
    @Test
    void testUpdate_diesWhenLifeRunsOut() {
        EnemyBullet b = new EnemyBullet(0, 0, 10, 0, 2);
        FakeMap map = new FakeMap(true);

        b.update(2.0, map); // vida inicial = 2.0

        assertTrue(b.isDead());
    }

    /* ============================================================
       4) Si ya está muerta, update no hace nada
       ============================================================ */
    @Test
    void testUpdate_doesNothingIfAlreadyDead() {
        EnemyBullet b = new EnemyBullet(0, 0, 100, 0, 2);
        FakeMap map = new FakeMap(true);

        b.markDead();
        b.update(1.0, map);

        assertTrue(b.isDead());
        // posición no debe cambiar
        assertEquals(0, b.position.x, 0.001);
    }

    /* ============================================================
       5) La bala no se vuelve NaN si el target es exactamente el mismo punto
       ============================================================ */
    @Test
    void testUpdate_zeroLengthDirectionDoesNotMove() {
        EnemyBullet b = new EnemyBullet(50, 50, 50, 50, 1); // dx = dy = 0
        FakeMap map = new FakeMap(true);

        b.update(1.0, map);

        assertEquals(50, b.position.x);
        assertEquals(50, b.position.y);
        assertFalse(Double.isNaN(b.getVx()));
        assertFalse(Double.isNaN(b.getVy()));
    }


}
