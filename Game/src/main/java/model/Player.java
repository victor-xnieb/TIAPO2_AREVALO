package model;

public class Player extends Entity {

    private int health;
    private int maxHealth;
    private WeaponType currentWeapon;
    private int ammo;

    public Player(double x, double y) {
        super(x, y, 16);
        this.health = 3;
        maxHealth = 6;
        this.currentWeapon = WeaponType.RIFLE;
        this.ammo = 20;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void move(double dx, double dy) {
        position.x += dx;
        position.y += dy;
    }

    public WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    public void switchWeapon() {
        currentWeapon = (currentWeapon == WeaponType.RIFLE)
                ? WeaponType.REVOLVER
                : WeaponType.RIFLE;
    }

    public int getAmmo() {
        return ammo;
    }

    public void addAmmo(int amount) {
        ammo += amount;
        if (ammo < 0) {
            ammo = 0;
        }
    }

    public boolean consumeAmmo(int amount) {
        if (ammo >= amount) {
            ammo -= amount;
            return true;
        }
        return false;
    }



}
