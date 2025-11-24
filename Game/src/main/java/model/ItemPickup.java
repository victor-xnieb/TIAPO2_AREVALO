package model;

public class ItemPickup {

    private final Position spawnPos; // dónde vive este ítem en el mapa
    private final double radius;
    private final ItemType type;

    private boolean available;
    private double respawnTimer; // en segundos

    public ItemPickup(double x, double y, double radius, ItemType type, double respawnTime) {
        this.spawnPos = new Position(x, y);
        this.radius = radius;
        this.type = type;
        this.available = true;
        this.respawnTimer = 0;
        this.baseRespawnTime = respawnTime;
    }

    // si quieres que todos tengan el mismo respawn, puedes fijarlo aquí:
    private final double baseRespawnTime;

    public Position getPosition() { return spawnPos; }
    public double getRadius() { return radius; }
    public ItemType getType() { return type; }

    public boolean isAvailable() { return available; }

    public void pickUp() {
        available = false;
        respawnTimer = baseRespawnTime;
    }

    public void update(double delta) {
        if (!available) {
            respawnTimer -= delta;
            if (respawnTimer <= 0) {
                respawnTimer = 0;
                available = true;
            }
        }
    }
}
