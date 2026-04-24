package model.domain.creature.enemy;

import model.domain.Coordinates;
import model.domain.creature.Character;

/**
 * Базовый абстрактный класс противника.
 *
 * Содержит общие характеристики врагов: тип, здоровье, силу,
 * ловкость, координаты и уровень враждебности. От данного класса
 * наследуются конкретные типы противников, такие как зомби, вампир,
 * призрак, огр, змей-маг и мимик.
 *
 * @see model.domain.creature.Character
 * @see model.domain.creature.enemy.EnemyType
 */

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
