package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.Item;

import java.util.ArrayList;
import java.util.Random;

public class MasterItemFactory {
    private static class FactoryWeight {
        private final ItemFactory factory;
        private final int weight;

        FactoryWeight(ItemFactory factory, int weight) {
            this.factory = factory;
            this.weight = weight;
        }

        ItemFactory getFactory() { return factory; }
        int getWeight() { return weight; }
    }

    private final ArrayList<FactoryWeight> weightedFactories;
    private final Random rng;
    private final int level;

    public MasterItemFactory(Random rng, int difficulty, int level) {
        this.rng = rng;
        this.weightedFactories = new ArrayList<>();
        this.level = level;
        setupFactories(calcWeight(difficulty, level));
    }
    private void setupFactories(int[] weights) {
        weightedFactories.add(new FactoryWeight(new TreasureFactory(), weights[0]));
        weightedFactories.add(new FactoryWeight(new FoodFactory(), weights[1]));
        weightedFactories.add(new FactoryWeight(new ElixirFactory(), weights[2]));
        weightedFactories.add(new FactoryWeight(new ScrollFactory(), weights[3]));
        weightedFactories.add(new FactoryWeight(new WeaponFactory(), weights[4]));
    }

    private int[] calcWeight(int difficulty, int level) {
        int[] weight = new int[5];  // 0 - Сокровища, 1 - Еда, 2 - Эликсиры, 3 - Свитки, 4 - Оружие
        weight[0] = 5 * ((level / 3) + 1);
        weight[1] = 5 * ((level / 7 + difficulty) + 1);
        weight[2] = 4 * ((level / 6 + difficulty) + 1);
        weight[3] = 2 *  ((level / 8 + difficulty) + 1);
        weight[4] = 5 * ((level / 3) + 1);
        return weight;
    }

    public Item createRandomItem(Coordinates coor) {
        int totalWeight = weightedFactories.stream().mapToInt(FactoryWeight::getWeight).sum();
        int randomWeight = rng.nextInt(totalWeight);
        int currentWeight = 0;
        for (FactoryWeight factory : weightedFactories) {
            currentWeight += factory.getWeight();
            if (randomWeight < currentWeight) {
                return factory.getFactory().createItem(coor, rng, level);
            }
        }

        return weightedFactories.get(0).getFactory().createItem(coor, rng, level);
    }
}
