package model.domain.creature.enemy;

import model.domain.Coordinates;

public class Zombie extends Enemy {
    public Zombie(Coordinates coor, int maxH, int s, int d, int h) {
        super(coor, maxH, s, d, EnemyType.ZOMBIE, h);
    }
}