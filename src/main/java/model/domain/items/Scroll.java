package model.domain.items;

import model.domain.Coordinates;

/**
 * Свиток постоянного улучшения характеристик.
 *
 * При использовании навсегда увеличивает выбранную характеристику героя:
 * здоровье, силу или ловкость.
 *
 * @see model.domain.items.BoostType
 * @see model.domain.creature.player.Hero
 */

public class Scroll extends Item {
    private final BoostType boostType;
    private final int value;
    public Scroll(Coordinates coor, String name, BoostType boostType, int value) {
        super(coor, ItemType.SCROLL, name);
        this.boostType = boostType;
        this.value = value;
    }

    public BoostType getBoostType() { return boostType; }

    public int getValue() { return value; }

    @Override
    public String toString() {
        return String.format("%s (+%d %s)", getName(), getValue(), getBoostType().toString().toLowerCase());
    }
}
