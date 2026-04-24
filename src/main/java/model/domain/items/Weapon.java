package model.domain.items;

import model.domain.Coordinates;

/**
 * Оружие, используемое персонажем.
 *
 * При экипировке увеличивает боевые характеристики героя
 * и влияет на расчёт наносимого урона.
 *
 * @see model.domain.creature.player.Hero
 * @see model.domain.items.Item
 */

public class Weapon extends Item {
    private final int power;
    public Weapon(Coordinates coor, String name, int power) {
        super(coor, ItemType.WEAPON, name);
        this.power = power;
    }

    public int getPower() { return power; }

    @Override
    public String toString() {
        return String.format("%s (+%d str)", getName(), getPower());
    }
}
