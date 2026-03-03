package model.domain.items;

import model.domain.Coordinates;
import model.domain.Entity;

public abstract class Item extends Entity {
    private ItemType type;
    private String name;

    public Item(Coordinates coor, ItemType type, String name) {
        super(coor);
        this.type = type;
        this.name = name;
    }

    public ItemType getType() { return type; }
    public String getName() { return name; }
}
