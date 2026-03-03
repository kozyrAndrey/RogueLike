package model.domain.items;

import model.domain.Coordinates;

public class Scroll extends Item {
    private final BoostType boostType;
    private final int value;
    public Scroll(Coordinates coor, String name, BoostType boostType, int value) {
        super(coor, ItemType.SCROLL, name);
        this.boostType = boostType;
        this.value = value;
    }

    public BoostType getBoostType() { return boostType; }

    public int getValue() { return value; }

    @Override
    public String toString() {
        return String.format("%s (+%d %s)", getName(), getValue(), getBoostType().toString().toLowerCase());
    }
}
