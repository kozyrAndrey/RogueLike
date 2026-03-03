package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;
import model.domain.creature.enemy.SnakeMage;

import java.util.Random;

public class SnakeMageFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(Coordinates coor, Random rng, int level) {
        return new SnakeMage(coor,
                EnemyFactory.calculateMaxHealth(EnemyType.SNAKE_MAGE, level),
                EnemyFactory.calculateStrength(EnemyType.SNAKE_MAGE, level),
                EnemyFactory.calculateDexterity(EnemyType.SNAKE_MAGE, level),
                EnemyFactory.calculateHostility(EnemyType.SNAKE_MAGE));
    }
}
