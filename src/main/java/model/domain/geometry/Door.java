package model.domain.geometry;

import model.domain.AccessLevel;
import model.domain.Coordinates;
import model.domain.Entity;

public class Door extends Entity {
    private final AccessLevel accessLevel;
    public Door(Coordinates coor, AccessLevel accessLevel) {
        super(coor);
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() { return accessLevel; }
}
