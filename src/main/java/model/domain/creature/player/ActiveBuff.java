package model.domain.creature.player;

import model.domain.items.BoostType;

public class ActiveBuff {
    private final BoostType type;
    private final int value;
    private int time;

    public ActiveBuff(BoostType type, int value, int time) {
        this.type = type;
        this.value = value;
        this.time = time;
    }

    public BoostType getType() { return type; }
    public int getValue() { return value; }

    public int getTime() { return time; }
    public void reduceTime() { this.time--; }
}
