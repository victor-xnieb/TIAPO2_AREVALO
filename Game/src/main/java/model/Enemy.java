package model;

import java.util.concurrent.ThreadLocalRandom;

public class Enemy extends Entity {

    private final EnemyType type;
    private int health;
    private double shootCooldown;


    public Enemy(double x, double y, EnemyType type) {
        super(x, y, 10);
        this.type = type;
        this.health = type.maxHealth;
        this.shootCooldown = Math.random() * 1.5; // para que no disparen todos al mismo tiempo
    }


    public EnemyType getType() {
        return type;
    }

    public EnemyBullet updateTowards(Player player, double delta, GameMap map) {
        double dx = player.getPosition().x - position.x;
        double dy = player.getPosition().y - position.y;
        double len = Math.sqrt(dx * dx + dy * dy);

        if (len > 0.0001) {
            dx /= len;
            dy /= len;
            double newX = position.x + dx * type.speed * delta;
            double newY = position.y + dy * type.speed * delta;

            if (map.canMoveTo(newX, newY, radius)) {
                position.x = newX;
                position.y = newY;
            }
        }

        // lógica de disparo (solo si tiene arma)
        if (type.weapon != null) {
            shootCooldown -= delta;
            if (shootCooldown <= 0) {
                double interval = (type.weapon == WeaponType.REVOLVER) ? 1.2 : 1.8;
                shootCooldown = interval;

                // crear bala hacia el jugador
                return new EnemyBullet(
                        position.x,
                        position.y,
                        player.getPosition().x,
                        player.getPosition().y,
                        type.damage  // daño por bala
                );
            }
        }

        return null;
    }



    public void takeDamage(int amount) {
        health -= amount;
    }

    public boolean isDead() {
        return health <= 0;
    }

}
