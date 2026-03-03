package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.Food;
import model.domain.items.Item;

import java.util.Random;

public class FoodFactory implements ItemFactory {
    private static final String[] FOOD_NAMES = {"Apple", "Bread", "Meat", "Cheese"};
    private static final int[] HEALTH_VALUES = {10, 15, 20, 25};

    @Override
    public Item createItem(Coordinates coor, Random rng, int level) {
        int index = rng.nextInt(FOOD_NAMES.length);
        return new Food(coor, FOOD_NAMES[index], HEALTH_VALUES[index] * Math.max(1, (level / 6)));
    }
}
