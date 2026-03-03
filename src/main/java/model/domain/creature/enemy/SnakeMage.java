package model.domain.creature.enemy;

import model.domain.Coordinates;

public class SnakeMage extends Enemy {
    public SnakeMage(Coordinates coor, int maxH, int s, int d, int h) {
        super(coor, maxH, s, d, EnemyType.SNAKE_MAGE, h);
    }
}
