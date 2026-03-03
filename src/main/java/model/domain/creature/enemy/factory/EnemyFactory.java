package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.enemy.EnemyType;

import java.util.Random;

public interface EnemyFactory {
    static final int LOW_STAT = 5, MEDIUM_STAT = 10, HIGH_STAT = 15, VERY_HIGH_STAT = 20;
    enum EnemyStrengthScale {
        EASY(5), MEDIUM(4), HARD(3);

        private final int value;

        EnemyStrengthScale(int value) {
            this.value = value;
        }
    }

    enum EnemyBaseStats {
        ZOMBIE(MEDIUM_STAT, LOW_STAT, HIGH_STAT, MEDIUM_STAT),
        VAMPIRE(MEDIUM_STAT, HIGH_STAT, HIGH_STAT,HIGH_STAT),
        GHOST(LOW_STAT, HIGH_STAT, LOW_STAT, LOW_STAT),
        OGRE(VERY_HIGH_STAT, LOW_STAT, VERY_HIGH_STAT, MEDIUM_STAT),
        SNAKE_MAGE(MEDIUM_STAT, VERY_HIGH_STAT, MEDIUM_STAT, HIGH_STAT),
        MIMIC(LOW_STAT, HIGH_STAT, HIGH_STAT, LOW_STAT);

        private final int strength;
        private final int dexterity;
        private final int maxHealth;
        private final int hostility;

        EnemyBaseStats(int strength, int dexterity, int maxHealth, int hostility) {
            this.strength = strength;
            this.dexterity = dexterity;
            this.maxHealth = maxHealth;
            this.hostility = hostility / 2;
        }
    }
    Enemy createEnemy(Coordinates coor, Random rng, int level);

    static int calculateStrength(EnemyType type, int level) {
        return switch (type) {
            case ZOMBIE -> EnemyBaseStats.ZOMBIE.strength * ((level / EnemyStrengthScale.EASY.value) + 1);
            case VAMPIRE -> EnemyBaseStats.VAMPIRE.strength * ((level / EnemyStrengthScale.MEDIUM.value) + 1);
            case GHOST -> EnemyBaseStats.GHOST.strength * ((level / EnemyStrengthScale.EASY.value) + 1);
            case OGRE -> EnemyBaseStats.OGRE.strength * ((level / EnemyStrengthScale.HARD.value) + 1);
            case SNAKE_MAGE -> EnemyBaseStats.SNAKE_MAGE.strength * ((level / EnemyStrengthScale.HARD.value) + 1);
            case MIMIC -> EnemyBaseStats.MIMIC.strength * ((level / EnemyStrengthScale.MEDIUM.value) + 1);
        };
    }

    static int calculateDexterity(EnemyType type, int level) {
        return switch (type) {
            case ZOMBIE -> EnemyBaseStats.ZOMBIE.dexterity * level;
            case VAMPIRE -> EnemyBaseStats.VAMPIRE.dexterity * level;
            case GHOST -> EnemyBaseStats.GHOST.dexterity * level;
            case OGRE -> EnemyBaseStats.OGRE.dexterity  * level;
            case SNAKE_MAGE -> EnemyBaseStats.SNAKE_MAGE.dexterity * level;
            case MIMIC -> EnemyBaseStats.MIMIC.dexterity * level;
        };
    }

    static int calculateMaxHealth(EnemyType type, int level) {
        return switch (type) {
            case ZOMBIE -> EnemyBaseStats.ZOMBIE.maxHealth * level;
            case VAMPIRE -> EnemyBaseStats.VAMPIRE.maxHealth * level;
            case GHOST -> EnemyBaseStats.VAMPIRE.maxHealth * level;
            case OGRE -> EnemyBaseStats.OGRE.maxHealth * level;
            case SNAKE_MAGE -> EnemyBaseStats.SNAKE_MAGE.maxHealth * level;
            case MIMIC -> EnemyBaseStats.MIMIC.maxHealth * level;
        };
    }

    static int calculateHostility(EnemyType type) {
        return switch (type) {
            case ZOMBIE -> EnemyBaseStats.ZOMBIE.hostility;
            case VAMPIRE -> EnemyBaseStats.VAMPIRE.hostility;
            case GHOST -> EnemyBaseStats.GHOST.hostility;
            case OGRE -> EnemyBaseStats.OGRE.hostility;
            case SNAKE_MAGE -> EnemyBaseStats.SNAKE_MAGE.hostility;
            case MIMIC -> EnemyBaseStats.MIMIC.hostility;
        };
    }
}
