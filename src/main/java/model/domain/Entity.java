package model.domain;

public abstract class Entity {
    protected Coordinates coor;
    public Entity(Coordinates coor) {
        this.coor = coor;
    }

    public Coordinates getCoor() { return coor; }

    public void setCoor(Coordinates coor) { this.coor = coor; }
}
