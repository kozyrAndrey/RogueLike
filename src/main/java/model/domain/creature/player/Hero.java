package model.domain.creature.player;

import model.domain.Coordinates;
import model.domain.creature.Character;
import model.domain.items.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Hero extends Character {
    private final String name;
    private int value;
    private boolean[] keys;
    private Backpack weaponBag;
    private Backpack foodBag;
    private Backpack scrollBag;
    private Backpack elixirBag;

    private Weapon equippedWeapon;

    private boolean sleep;
    private LinkedList<ActiveBuff> buffs = new LinkedList<>();

    public Hero(Coordinates coor, int maxH, int s, int d, int h, String name, int value) {
        super(coor, maxH, s, d);
        this.name = name;
        this.health = h;
        this.value = value;
        this.keys = new boolean[3];

        this.weaponBag = new Backpack(ItemType.WEAPON);
        this.foodBag = new Backpack(ItemType.FOOD);
        this.scrollBag = new Backpack(ItemType.SCROLL);
        this.elixirBag = new Backpack(ItemType.ELIXIR);
    }

    public String getName() { return name; }

    @Override
    public int getStrength() {
        int baseStrength = super.getStrength();
        if (equippedWeapon != null) return baseStrength + equippedWeapon.getPower();
        return baseStrength;
    }
    public int getBaseStrength() { return super.getStrength(); }
    public int getValue() { return value; }
    public void addValue(int value) { this.value += value; }
    public boolean[] getKeys() { return keys; }

    public boolean addItemToBackpack(Item item) {
        System.out.println("Adding item to backpack: " + item.getName());
        switch (item.getType()) {
            case WEAPON:
                return weaponBag.addItem(item);
            case FOOD:
                return foodBag.addItem(item);
            case SCROLL:
                return scrollBag.addItem(item);
            case ELIXIR:
                return elixirBag.addItem(item);
            default:
                return false;
        }
    }

    public boolean removeItemFromBackpack(Item item) {
        System.out.println("Removing item from backpack: " + item.getName());
        switch (item.getType()) {
            case WEAPON:
                return weaponBag.removeItem(item);
            case FOOD:
                return foodBag.removeItem(item);
            case SCROLL:
                return scrollBag.removeItem(item);
            case ELIXIR:
                return elixirBag.removeItem(item);
            default:
                return false;
        }
    }

    public List<Item> getItemsByType(ItemType itemType) {
        System.out.println("Getting items of type: " + itemType);
        switch (itemType) {
            case WEAPON:
                return weaponBag.getItems();
            case FOOD:
                return foodBag.getItems();
            case SCROLL:
                return scrollBag.getItems();
            case ELIXIR:
                return elixirBag.getItems();
            default:
                return new ArrayList<>();
        }
    }

    public void equipWeapon(Weapon weapon) {
        if (weaponBag.getItems().contains(weapon)) {
            this.equippedWeapon = weapon;
        }
    }

    public Weapon unequipWeapon() {
        Weapon droppedWeapon = this.equippedWeapon;
        this.equippedWeapon = null;
        return droppedWeapon;
    }

    public Weapon getEquippedWeapon() { return equippedWeapon; }

    public void addBuff(BoostType type, int value, int time) {
        buffs.add(new ActiveBuff(type, value, time));
    }

    public void timeHasPassed() {
        ListIterator<ActiveBuff> buffIterator = buffs.listIterator();
        while (buffIterator.hasNext()) {
            ActiveBuff buff = buffIterator.next();
            buff.reduceTime();
            if (buff.getTime() <= 0) {
                int value = buff.getValue();
                switch (buff.getType()) {
                    case HEALTH -> {
                        this.setMaxHealth(Math.max(1, this.getMaxHealth() - value));
                        if (this.getHealth() > this.getMaxHealth()) this.setHealth(this.getMaxHealth());
                    }
                    case STRENGTH -> this.setStrength(this.getBaseStrength() - value);
                    case DEXTERITY -> this.setDexterity(this.getDexterity() - value);
                }
                buffIterator.remove();
            }
        }
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public List<ActiveBuff> getActiveBuffs() {
        return new LinkedList<>(buffs);
    }

    public void setKeys(boolean[] keys) {
        this.keys = keys.clone();
    }
}
