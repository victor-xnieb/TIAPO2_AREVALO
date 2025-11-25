package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    /* ============================================================
       1) takeDamage reduce salud y no baja de 0
       ============================================================ */
    @Test
    void testTakeDamage_reducesHealthAndStopsAtZero() {
        Player p = new Player(0, 0);

        p.takeDamage(2);
        assertEquals(1, p.getHealth());

        p.takeDamage(10);  // exceso
        assertEquals(0, p.getHealth());
        assertTrue(p.isDead());
    }

    /* ============================================================
       2) heal no excede maxHealth
       ============================================================ */
    @Test
    void testHeal_doesNotExceedMaxHealth() {
        Player p = new Player(0, 0);

        p.takeDamage(2);      // baja de 3 a 1
        p.heal(5);            // intenta pasar de 3
        assertEquals(3, p.getHealth());
    }

    /* ============================================================
       3) move cambia la posición correctamente
       ============================================================ */
    @Test
    void testMove_updatesPosition() {
        Player p = new Player(10, 20);
        p.move(5, -10);

        assertEquals(15, p.position.x, 0.001);
        assertEquals(10, p.position.y, 0.001);
    }

    /* ============================================================
       4) switchWeapon alterna entre REVOLVER y RIFLE
       ============================================================ */
    @Test
    void testSwitchWeapon_togglesCorrectly() {
        Player p = new Player(0, 0);

        assertEquals(WeaponType.REVOLVER, p.getCurrentWeapon());
        p.switchWeapon();
        assertEquals(WeaponType.RIFLE, p.getCurrentWeapon());
        p.switchWeapon();
        assertEquals(WeaponType.REVOLVER, p.getCurrentWeapon());
    }

    /* ============================================================
       5) reload carga desde la reserva y respeta la capacidad
       ============================================================ */
    @Test
    void testReload_fillsMagazineCorrectly() {
        Player p = new Player(0, 0);

        // Vaciamos revólver para probar recarga
        while (p.tryShoot()) {
            // vaciar cargador
        }

        assertEquals(0, p.getMagazineAmmo());
        assertTrue(p.getReserveAmmo() > 0);

        boolean reloaded = p.reload();

        assertTrue(reloaded);
        assertEquals(9, p.getMagazineAmmo()); // capacidad del revólver
    }


}
