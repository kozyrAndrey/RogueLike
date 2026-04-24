package model.domain.items;

import model.domain.Coordinates;
import model.domain.Entity;

/**
 * Базовый класс игрового предмета.
 *
 * Описывает общие свойства предметов: координаты, название,
 * тип и значение. Используется как родительский класс для оружия,
 * еды, эликсиров, свитков, сокровищ и ключей.
 *
 * @see model.domain.items.Weapon
 * @see model.domain.items.Food
 * @see model.domain.items.Elixir
 * @see model.domain.items.Scroll
 * @see model.domain.items.Treasure
 * @see model.domain.items.Key
 */

public abstract class Item extends Entity {
    private ItemType type;
    private String name;

    public Item(Coordinates coor, ItemType type, String name) {
        super(coor);
        this.type = type;
        this.name = name;
    }

    public ItemType getType() { return type; }
    public String getName() { return name; }
}
