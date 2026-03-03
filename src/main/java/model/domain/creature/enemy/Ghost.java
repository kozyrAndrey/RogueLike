package model.domain.creature.enemy;

import model.domain.Coordinates;

public class Ghost extends Enemy {

    private boolean isInvisible = false;
    public Ghost(Coordinates coor, int maxH, int s, int d, int h) {
        super(coor, maxH, s, d, EnemyType.GHOST, h);
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public void setInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }
}
