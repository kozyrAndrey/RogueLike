package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;
import model.domain.creature.enemy.Mimic;

import java.util.Random;

public class MimicFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(Coordinates coor, Random rng, int level) {
        return new Mimic(coor,
                EnemyFactory.calculateMaxHealth(EnemyType.MIMIC, level),
                EnemyFactory.calculateStrength(EnemyType.MIMIC, level),
                EnemyFactory.calculateDexterity(EnemyType.MIMIC, level),
                EnemyFactory.calculateHostility(EnemyType.MIMIC));
    }
}
