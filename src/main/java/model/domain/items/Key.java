package model.domain.items;

import model.domain.AccessLevel;
import model.domain.Coordinates;

public class Key extends Item {
    private final AccessLevel access;
    public Key(Coordinates coor, AccessLevel access, String name) {
        super(coor, ItemType.KEY, name);
        this.access = access;
    }

    public AccessLevel getAccess() { return access; }
}
