package model.domain.items;

import model.domain.Coordinates;

public class Treasure extends Item {
    private final int value;
    public Treasure(Coordinates coor, int value) {
        super(coor, ItemType.TREASURE, (value + " Gold"));
        this.value = value;
    }

    public int getValue() { return value; }
}
