package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.BoostType;
import model.domain.items.Elixir;
import model.domain.items.Item;

import java.util.Random;

public class ElixirFactory implements ItemFactory {
    private static final String[] ELIXIR_NAMES = {"Health Potion", "Potion of giant strength", "Potion of speed"};

    @Override
    public Item createItem(Coordinates coor, Random rng, int level) {
        int index = rng.nextInt(ELIXIR_NAMES.length);
        BoostType type = BoostType.HEALTH;
        switch (index) {
            case 1 -> type = BoostType.STRENGTH;
            case 2 -> type = BoostType.DEXTERITY;
        }
        return new Elixir(coor, ELIXIR_NAMES[index], type, rng.nextInt(3 * level, 10 * level));
    }
}
