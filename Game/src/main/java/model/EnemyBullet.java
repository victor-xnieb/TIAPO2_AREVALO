package model;

public class EnemyBullet extends Entity {

    private double vx;
    private double vy;
    private double life;  // segundos que vive
    private boolean dead;
    private final int damage;

    public EnemyBullet(double startX, double startY,
                       double targetX, double targetY,
                       int damage) {
        super(startX, startY, 3); // radio pequeño

        this.damage = damage;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) {
            vx = 0;
            vy = 0;
        } else {
            double speed = 260; // velocidad de la bala enemiga
            vx = dx / len * speed;
            vy = dy / len * speed;
        }

        life = 2.0;
        dead = false;
    }

    public void update(double delta, GameMap map) {
        if (dead) return;

        double newX = position.x + vx * delta;
        double newY = position.y + vy * delta;

        // si choca con pared, desaparece
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

    public int getDamage() {
        return damage;
    }
}
