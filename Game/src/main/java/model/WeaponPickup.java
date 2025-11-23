package model;

public class WeaponPickup {

    private final Position position;
    private final double radius;
    private final WeaponType weaponType;

    public WeaponPickup(double x, double y, double radius, WeaponType weaponType) {
        this.position = new Position(x, y);
        this.radius = radius;
        this.weaponType = weaponType;
    }

    public Position getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }
}
