package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;
import model.domain.creature.enemy.Ogre;

import java.util.Random;

public class OgreFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(Coordinates coor, Random rng, int level) {
        return new Ogre(coor,
                EnemyFactory.calculateMaxHealth(EnemyType.OGRE, level),
                EnemyFactory.calculateStrength(EnemyType.OGRE, level),
                EnemyFactory.calculateDexterity(EnemyType.OGRE, level),
                EnemyFactory.calculateHostility(EnemyType.OGRE));
    }
}
