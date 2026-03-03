package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;
import model.domain.creature.enemy.Zombie;

import java.util.Random;

public class ZombieFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(Coordinates coor, Random rng, int level) {
        return new Zombie(coor,
                EnemyFactory.calculateMaxHealth(EnemyType.ZOMBIE, level),
                EnemyFactory.calculateStrength(EnemyType.ZOMBIE, level),
                EnemyFactory.calculateDexterity(EnemyType.ZOMBIE, level),
                EnemyFactory.calculateHostility(EnemyType.ZOMBIE));
    }
}
