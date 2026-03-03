package model.domain.creature.enemy;

import model.domain.Coordinates;

import java.util.Random;

public class Mimic extends Enemy {
    private static final Random rng = new Random();
    private boolean hiding = true;
    private int hidingSymbol;

    public Mimic(Coordinates coor, int maxH, int s, int d, int h) {
        super(coor, maxH, s, d, EnemyType.MIMIC, h);
        hidingSymbol = rng.nextInt(11, 16);
    }

    public boolean isMimicHiding() {
        return hiding;
    }

    public void setMimicHiding(boolean hiding) {
        this.hiding = hiding;
    }

    public int getHidingSymbol() {
        return hidingSymbol;
    }
}
