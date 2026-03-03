package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;
import model.domain.creature.enemy.Ghost;

import java.util.Random;

public class GhostFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(Coordinates coor, Random rng, int level) {
        return new Ghost(coor,
                EnemyFactory.calculateMaxHealth(EnemyType.GHOST, level),
                EnemyFactory.calculateStrength(EnemyType.GHOST, level),
                EnemyFactory.calculateDexterity(EnemyType.GHOST, level),
                EnemyFactory.calculateHostility(EnemyType.GHOST));
    }
}
