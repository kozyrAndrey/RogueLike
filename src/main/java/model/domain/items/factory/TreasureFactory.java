package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.Item;
import model.domain.items.Treasure;

import java.util.Random;

public class TreasureFactory implements ItemFactory {
    @Override
    public Item createItem(Coordinates coor, Random rng, int level) {
        int baseValue = 50;
        int randomBonus = rng.nextInt(100);
        return new Treasure(coor, (baseValue + randomBonus * level));
    }
}
