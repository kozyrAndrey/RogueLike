package model.domain.creature.enemy;

import model.domain.Coordinates;

public class Vampire extends Enemy {
    public Vampire(Coordinates coor, int maxH, int s, int d, int h) {
        super(coor, maxH, s, d, EnemyType.VAMPIRE, h);
    }

    private boolean firstAttack = true;

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }
}
