package model.domain.creature.enemy.factory;

import model.domain.Coordinates;
import model.domain.creature.enemy.Enemy;

import java.util.ArrayList;
import java.util.Random;

public class MasterEnemyFactory {
    private static class FactoryWeight {
        private final EnemyFactory factory;
        private final int weight;

        FactoryWeight(EnemyFactory factory, int weight) {
            this.factory = factory;
            this.weight = weight;
        }

        EnemyFactory getFactory() { return factory; }
        int getWeight() { return weight; }
    }

    private final ArrayList<FactoryWeight> weightedFactories;
    private final Random rng;
    private final int level;

    public MasterEnemyFactory(Random rng, int difficulty, int level) {
        this.rng = rng;
        this.weightedFactories = new ArrayList<>();
        this.level = level;
        setupFactories(calcWeight(difficulty, level));
    }

    private int[] calcWeight(int difficulty, int level) {
        int[] weight = new int[6];  // 0 - Зомби, 1 - Вампир, 2 - Призрак, 3 - Огр, 4 - Змея-маг, 5 - Мимик
        weight[0] = Math.max(5, 5 * (23 - (level + difficulty)));
        weight[1] = 5 * ((level / 4) + 1);
        weight[2] = Math.max(5, 5 * (23 - (level + difficulty) / 2));
        weight[3] = 5 * ((level / 2) + 1);
        weight[4] = 10 * ((level / 3) + 1);
        weight[5] = 5 * ((level / 3) + 1);
        return weight;
    }
    private void setupFactories(int[] weights) {
        weightedFactories.add(new FactoryWeight(new ZombieFactory(), weights[0]));
        weightedFactories.add(new FactoryWeight(new VampireFactory(), weights[1]));
        weightedFactories.add(new FactoryWeight(new GhostFactory(), weights[2]));
        weightedFactories.add(new FactoryWeight(new OgreFactory(), weights[3]));
        weightedFactories.add(new FactoryWeight(new SnakeMageFactory(), weights[4]));
        weightedFactories.add(new FactoryWeight(new MimicFactory(), weights[5]));
    }

    public Enemy createRandomEnemy(Coordinates coor) {
        int totalWeight = weightedFactories.stream().mapToInt(FactoryWeight::getWeight).sum();
        int randomWeight = rng.nextInt(totalWeight);
        int currentWeight = 0;
        for (FactoryWeight factory : weightedFactories) {
            currentWeight += factory.getWeight();
            if (randomWeight < currentWeight) {
                return factory.getFactory().createEnemy(coor, rng, level);
            }
        }
        return weightedFactories.get(0).getFactory().createEnemy(coor, rng, level);
    }
}
