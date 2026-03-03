package model.domain.creature.enemy;

import model.domain.Coordinates;
import model.domain.creature.Character;

public abstract class Enemy extends Character {
    private final EnemyType type;
    private final int hostility;

    Enemy(Coordinates coor, int maxH, int s, int d, EnemyType type, int h) {
        super(coor, maxH, s, d);
        this.type = type;
        this.hostility = h;
    }

    public int getHostility() { return hostility; }
    public EnemyType getType() { return type; }
}
