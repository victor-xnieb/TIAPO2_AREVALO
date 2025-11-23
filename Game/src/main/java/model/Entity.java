package model;

public abstract class Entity {

    protected Position position;
    protected double radius;

    public Entity(double x, double y, double radius) {
        this.position = new Position(x, y);
        this.radius = radius;
    }

    public Position getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public boolean collidesWith(Entity other) {
        double dx = position.x - other.position.x;
        double dy = position.y - other.position.y;
        double dist2 = dx * dx + dy * dy;
        double r = radius + other.radius;
        return dist2 <= r * r;
    }
}
