package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.Item;
import model.domain.items.Weapon;

import java.util.Random;

public class WeaponFactory implements ItemFactory {
    private static final String[] WEAPON_NAMES = { "Iron Axe", "Moonlight GreatSword", "Stick of Justice", "Doom Hammer"};
    @Override
    public Item createItem(Coordinates coor, Random rng, int level) {
        int index = rng.nextInt(WEAPON_NAMES.length);
        return new Weapon(coor, WEAPON_NAMES[index], rng.nextInt(level * 2, level * 10));
    }
}
