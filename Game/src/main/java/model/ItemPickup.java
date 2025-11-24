package model;

/**
 * Ítems del mapa (comida, curación, llaves, portal, etc.)
 * Extiende Entity para poder usar collidesWith(...) y tener position/radius.
 */
public class ItemPickup extends Entity {

    private final ItemType type;

    // tiempo base que debe pasar para que el ítem reaparezca
    private final double baseRespawnTime;

    // si > 0, está contando para reaparecer
    private double respawnTimer;

    // true -> visible / se puede recoger
    // false -> consumido, esperando respawn
    private boolean active;

    /**
     * @param x            posición inicial X en coordenadas de mundo
     * @param y            posición inicial Y en coordenadas de mundo
     * @param radius       radio de colisión/dibujo
     * @param type         tipo de ítem (FOOD, MEDICINE, KEY, PORTAL, etc.)
     * @param respawnTime  segundos que tarda en reaparecer después de consumido
     */
    public ItemPickup(double x,
                      double y,
                      double radius,
                      ItemType type,
                      double respawnTime) {

        super(x, y, radius);      // Entity maneja position y radius
        this.type = type;
        this.baseRespawnTime = respawnTime;
        this.respawnTimer = 0.0;
        this.active = true;       // empieza disponible
    }

    // ------------ getters básicos ------------

    public ItemType getType() {
        return type;
    }

    /** Compatibilidad con código viejo que preguntaba "isAvailable". */
    public boolean isAvailable() {
        return active;
    }

    /** Nombre nuevo que usamos para llaves/portal: */
    public boolean isActive() {
        return active;
    }

    // ------------ ciclo de vida del ítem ------------

    /** Marca el ítem como consumido y arranca el timer de respawn. */
    public void consume() {
        active = false;
        respawnTimer = baseRespawnTime;
    }

    /** Alias por si en algún sitio aún llamas pickUp(): */
    public void pickUp() {
        consume();
    }

    /**
     * Actualiza el estado del ítem (solo respawn).
     * Llamado desde GameController.updateXxxPickups(delta).
     */
    public void update(double delta) {
        // Solo respawnean los ítems que tengan tiempo base > 0
        if (!active && baseRespawnTime > 0.0) {
            respawnTimer -= delta;
            if (respawnTimer <= 0.0) {
                respawnTimer = 0.0;
                active = true;
            }
        }
    }
}
