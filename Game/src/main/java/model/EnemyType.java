package model;

public enum EnemyType {
    BANDIT_REVOLVER(1, 60, WeaponType.REVOLVER, 2),
    BANDIT_MELEE(1, 90, null, 2),
    OUTLAW_RIFLE(2, 45, WeaponType.RIFLE, 3),
    HUNTER(1, 75, WeaponType.REVOLVER, 2),
    GUARD(1, 40, WeaponType.RIFLE, 4),
    SCOUT(1, 110, WeaponType.REVOLVER, 1);

    public final int damage;       // daño al jugador
    public final double speed;
    public final WeaponType weapon;
    public final int maxHealth;    // vida del enemigo

    EnemyType(int damage, double speed, WeaponType weapon, int maxHealth) {
        this.damage = damage;
        this.speed = speed;
        this.weapon = weapon;
        this.maxHealth = maxHealth;
    }
}
