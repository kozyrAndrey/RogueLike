package model.domain.items;

import model.domain.Coordinates;

/**
 * Еда, восстанавливающая здоровье героя.
 *
 * После использования удаляется из инвентаря и увеличивает
 * текущее здоровье персонажа на заданное значение.
 *
 * @see model.domain.creature.player.Hero
 * @see model.domain.items.Item
 */

public class Food extends Item {
    private final int value;

    public Food(Coordinates coor, String name, int value) {
        super(coor, ItemType.FOOD, name);
        this.value = value;
    }

    public int getValue() { return value; }

    @Override
    public String toString() {
        return String.format("%s (+%d)", getName(), getValue());
    }
}
