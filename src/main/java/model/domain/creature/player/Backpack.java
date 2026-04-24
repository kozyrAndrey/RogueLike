package model.domain.creature.player;

import model.domain.items.Item;
import model.domain.items.ItemType;

import java.util.ArrayList;

/**
 * Рюкзак для хранения предметов одного типа.
 *
 * Каждый экземпляр рюкзака принимает только предметы заданного типа
 * и ограничивает максимальное количество предметов. Используется
 * персонажем для хранения оружия, еды, свитков и эликсиров.
 *
 * @see model.domain.items.Item
 * @see model.domain.items.ItemType
 */

public class Backpack {
    private final ArrayList<Item> items;
    private final int capacity = 9;
    private final ItemType allowedType;

    public Backpack(ItemType allowedType) {
        this.allowedType = allowedType;
        this.items = new ArrayList<>(capacity);
    }

/**
 * Добавляет предмет в рюкзак.
 *
 * Предмет добавляется только в том случае, если в рюкзаке есть место
 * и тип предмета совпадает с разрешённым типом данного рюкзака.
 *
 * @param item предмет для добавления
 * @return true, если предмет был добавлен; иначе false
 */

    public boolean addItem(Item item) {
        if (items.size() < capacity && item.getType() == allowedType) {
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public Item getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public ArrayList<Item> getItems() { return new ArrayList<>(items); }

    public int getSize() { return items.size(); }
    public int getCapacity() { return capacity; }
    public ItemType getAllowedType() { return allowedType; }
    public boolean isFull() { return items.size() >= capacity; }
    public void clear() { items.clear(); }
}
