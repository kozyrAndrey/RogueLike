package model.domain.creature;

import model.domain.Coordinates;
import model.domain.Entity;
import model.domain.creature.enemy.EnemyType;

public abstract class Character extends Entity {
    private int maxHealth;
    protected int health;
    private int strength;
    private int dexterity;

    public Character(Coordinates coor, int maxH, int s, int d) {
        super(coor);
        this.maxHealth = maxH;
        this.health = maxH;
        this.strength = s;
        this.dexterity = d;
    }

    public Character(Coordinates coor) {
        super(coor);
    }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getHealth() { return health; }
    public void setHealth(int health) {
        if (health < maxHealth) {
            this.health = health;
        } else {
            this.health = maxHealth;
        }
    }
    public void reduceMaxHealth(int minus){
        this.maxHealth = this.maxHealth - minus;
    }
    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }
    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }
}
