package model.domain.items;

import model.domain.Coordinates;

/**
 * Эликсир временного усиления характеристик.
 *
 * При использовании накладывает на героя временный эффект,
 * который действует ограниченное количество ходов.
 *
 * @see model.domain.items.BoostType
 * @see model.domain.creature.player.ActiveBuff
 */

public class Elixir extends Item {
    private final BoostType boostType;
    private final int value;
    public Elixir(Coordinates coor, String name, BoostType boostType, int value) {
        super(coor, ItemType.ELIXIR, name);
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
