package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.BoostType;
import model.domain.items.Item;
import model.domain.items.Scroll;

import java.util.Random;

public class ScrollFactory implements ItemFactory {
    private static final String[] SCROLL_NAMES = {"Scroll of bear", "Scroll of power", "Scroll of snake"};
    @Override
    public Item createItem(Coordinates coor, Random rng, int level) {
        int index = rng.nextInt(3);
        BoostType type = BoostType.HEALTH;
        switch (index) {
            case 1 -> {
                type = BoostType.STRENGTH;
            }
            case 2 -> {
                type = BoostType.DEXTERITY;
            }
        }
        return new Scroll(coor, SCROLL_NAMES[index], type, rng.nextInt(level, 5 * level));
    }
}
