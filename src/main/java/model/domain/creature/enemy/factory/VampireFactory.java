package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;
import model.domain.creature.enemy.Vampire;

import java.util.Random;

public class VampireFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(Coordinates coor, Random rng, int level) {
        return new Vampire(coor,
                EnemyFactory.calculateMaxHealth(EnemyType.VAMPIRE, level),
                EnemyFactory.calculateStrength(EnemyType.VAMPIRE, level),
                EnemyFactory.calculateDexterity(EnemyType.VAMPIRE, level),
                EnemyFactory.calculateHostility(EnemyType.VAMPIRE));
    }
}
