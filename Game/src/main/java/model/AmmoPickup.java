package model;

public class AmmoPickup extends Entity {

    private final int amount;

    public AmmoPickup(double x, double y, int amount) {
        super(x, y, 10); // radio pequeño
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
