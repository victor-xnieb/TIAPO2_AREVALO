package model;

public class Bullet extends Entity {

    private double vx;
    private double vy;
    private double life; // en segundos
    private boolean dead;

    // 🔥 NUEVO: tipo de arma que disparó esta bala
    private WeaponType weaponType;

    /**
     * @param startX    posición inicial X de la bala
     * @param startY    posición inicial Y de la bala
     * @param targetX   punto al que se disparó (por ejemplo, mouseX)
     * @param targetY   punto al que se disparó (por ejemplo, mouseY)
     * @param weaponType arma que dispara (REVOLVER o RIFLE)
     */
    public Bullet(double startX,
                  double startY,
                  double targetX,
                  double targetY,
                  WeaponType weaponType) {

        super(startX, startY, 3); // radio pequeño

        this.weaponType = weaponType; // guardamos el arma

        double dx = targetX - startX;
        double dy = targetY - startY;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) {
            // evita NaN si haces click justo encima del jugador
            vx = 0;
            vy = 0;
        } else {
            double speed = 350; // píxeles por segundo
            vx = dx / len * speed;
            vy = dy / len * speed;
        }

        life = 2.0; // viven 2 segundos máximo
        dead = false;
    }

    public void update(double delta, GameMap map) {
        if (dead) return;

        double newX = position.x + vx * delta;
        double newY = position.y + vy * delta;

        // si choca con una pared, muere
        if (!map.canMoveTo(newX, newY, radius)) {
            dead = true;
            return;
        }

        position.x = newX;
        position.y = newY;

        life -= delta;
        if (life <= 0) {
            dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void markDead() {
        dead = true;
    }

    // 🔥 NUEVO: saber con qué arma salió la bala
    public WeaponType getWeaponType() {
        return weaponType;
    }

    // 🔥 NUEVO: daño según arma (rifle pega más)
    public int getDamage() {
        return (weaponType == WeaponType.RIFLE) ? 2 : 1;
    }
}
