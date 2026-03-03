package model.domain.creature.enemy;

import model.domain.Coordinates;

public class Ogre extends Enemy {
    public Ogre(Coordinates coor, int maxH, int s, int d, int h) {
        super(coor, maxH, s, d, EnemyType.OGRE, h);
    }

    private boolean rest = false;
    private boolean counterAttack = false;

    public boolean isRest() {
        return rest;
    }
    public boolean isCounterAttack() {
        return counterAttack;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }
    public void setCounterAttack(boolean counterAttack) {
        this.counterAttack = counterAttack;
    }
}
