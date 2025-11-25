package model;

public class Player extends Entity {

    // Salud
    private int health;
    private int maxHealth;

    // Arma actual
    private WeaponType currentWeapon;

    // Balas sueltas (reserva común a las dos armas)
    private int reserveAmmo;

    // Balas en los cargadores
    private int revolverMagAmmo;
    private int rifleMagAmmo;

    // Capacidades de cargador
    private static final int REVOLVER_MAG_CAP = 9; // revólver: 9 balas
    private static final int RIFLE_MAG_CAP    = 5; // rifle: 5 balas

    public Player(double x, double y) {
        super(x, y, 24);

        this.health    = 3;
        this.maxHealth = 3;

        // Empieza con revólver
        this.currentWeapon = WeaponType.REVOLVER;

        // Balas iniciales compartidas
        this.reserveAmmo    = 20;
        this.revolverMagAmmo = Math.min(REVOLVER_MAG_CAP, reserveAmmo);
        this.reserveAmmo    -= revolverMagAmmo;

        // Rifle empieza descargado
        this.rifleMagAmmo = 0;
    }

    // ======== Salud / vidas =========

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    // Para el HUD si usas "vidas"
    public int getLives() {
        return health;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health < 0) {
            health = 0;
        }
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    // ======== Movimiento =========

    public void move(double dx, double dy) {
        position.x += dx;
        position.y += dy;
    }

    // ======== Armas / munición =========

    public WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    public void switchWeapon() {
        currentWeapon = (currentWeapon == WeaponType.RIFLE)
                ? WeaponType.REVOLVER
                : WeaponType.RIFLE;
    }

    public void setCurrentWeapon(WeaponType weapon) {
        this.currentWeapon = weapon;
    }

    public String getCurrentWeaponName() {
        return (currentWeapon == WeaponType.RIFLE) ? "RIFLE" : "REVÓLVER";
    }

    /**
     * Total de balas (cargadores + reserva).
     * Esto es lo que muestras en el HUD como "Total".
     */
    public int getAmmo() {
        return reserveAmmo + revolverMagAmmo + rifleMagAmmo;
    }

    /**
     * Balas en el cargador del arma actual.
     * Esto es lo que muestras como "Cargador".
     */
    public int getMagazineAmmo() {
        if (currentWeapon == WeaponType.RIFLE) {
            return rifleMagAmmo;
        } else {
            return revolverMagAmmo;
        }
    }

    /**
     * Balas en la reserva (sin contar cargadores).
     */
    public int getReserveAmmo() {
        return reserveAmmo;
    }

    public int getCurrentMagCapacity() {
        return (currentWeapon == WeaponType.RIFLE)
                ? RIFLE_MAG_CAP
                : REVOLVER_MAG_CAP;
    }

    /**
     * Añadir balas a la reserva (por pickups).
     */
    public void addAmmo(int amount) {
        if (amount <= 0) return;
        reserveAmmo += amount;
    }

    /**
     * Intentar disparar 1 bala del cargador del arma actual.
     * Devuelve true si disparó, false si el cargador está vacío.
     */
    public boolean tryShoot() {
        if (currentWeapon == WeaponType.RIFLE) {
            if (rifleMagAmmo <= 0) {
                return false;
            }
            rifleMagAmmo--;
        } else {
            if (revolverMagAmmo <= 0) {
                return false;
            }
            revolverMagAmmo--;
        }
        // Como el total lo calculamos dinámicamente (reserva + mags),
        // no tenemos que tocar reserveAmmo aquí: ya bajó por sacar del cargador.
        return true;
    }

    /**
     * Recarga el arma actual desde la reserva.
     * NO toca el cargador del arma que no está equipada.
     * Devuelve true si recargó algo.
     */
    public boolean reload() {
        int cap = getCurrentMagCapacity();

        if (currentWeapon == WeaponType.RIFLE) {
            if (rifleMagAmmo >= cap) {
                return false; // ya está lleno
            }
            if (reserveAmmo <= 0) {
                return false; // no hay balas en reserva
            }
            int need          = cap - rifleMagAmmo;
            int bulletsToLoad = Math.min(need, reserveAmmo);

            rifleMagAmmo += bulletsToLoad;
            reserveAmmo   -= bulletsToLoad;

            return bulletsToLoad > 0;

        } else { // REVOLVER
            if (revolverMagAmmo >= cap) {
                return false;
            }
            if (reserveAmmo <= 0) {
                return false;
            }
            int need          = cap - revolverMagAmmo;
            int bulletsToLoad = Math.min(need, reserveAmmo);

            revolverMagAmmo += bulletsToLoad;
            reserveAmmo      -= bulletsToLoad;

            return bulletsToLoad > 0;
        }
    }

}
