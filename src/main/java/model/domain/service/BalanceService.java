package model.domain.service;

import model.domain.creature.player.Hero;
import model.domain.items.Food;
import model.domain.items.Item;
import model.domain.items.ItemType;

import java.util.List;
import java.util.Random;

public class BalanceService {
    private final Hero player;
    private final int prevDifficulty, maxHpOnStart, startValue;

    public static int calculateNumberOfEnemies(int level, int difficulty) {
        int number = 1;
        Random rng = new Random();
        if (level <= 7) {
            number += rng.nextInt(2);
        } else if (level <= 14) {
            number = 2;
        } else {
            number = 2;
            number += rng.nextInt(2);
        }
        switch (difficulty) {
            case -2 -> {
                if (number != 1)
                    number--;
            }
            case -1 -> {
                if (number != 1)
                    number -= rng.nextInt(2);
            }
            case 1 -> number += rng.nextInt(2);
            case 2 -> number++;
        }
        return number;
    }

    public static int calculateNumberOfItems(int level, int difficulty) {
        return 5 - ((level - 1) / 6) + new Random().nextInt(2);
    }

    public BalanceService(Hero player, int difficulty) {
        this.player = player;
        this.prevDifficulty = difficulty;
        this.maxHpOnStart = player.getMaxHealth();
        this.startValue = calcValue();
    }

    // Конструктор для загрузки
    public BalanceService(Hero player, int difficulty, int maxHpOnStart, int startValue) {
        this.player = player;
        this.prevDifficulty = difficulty;
        this.maxHpOnStart = maxHpOnStart;
        this.startValue = startValue;
    }

    public int getPrevDifficulty() {
        return prevDifficulty;
    }

    public int getMaxHpOnStart() {
        return maxHpOnStart;
    }

    public int getStartValue() {
        return startValue;
    }

    private int calcValue() {
        int hp = player.getHealth();
        List<Item> backpackWithFood = player.getItemsByType(ItemType.FOOD);
        int foodHp = 0;
        for (int i = 0; i < backpackWithFood.size(); i++) {
            Food curFood = (Food) backpackWithFood.get(i);
            foodHp += curFood.getValue();
        }
        double value = ((double) (hp + foodHp) / player.getMaxHealth()) * 100;
        return (int) value;
    }

    public int calcDifficultyForNextLevel() {
        int scale = 0;
        int currentMaxHp = player.getMaxHealth();
        // Посмотреть максимальное хп
        if (currentMaxHp * 1.25 < maxHpOnStart)
            scale--;
        if (maxHpOnStart * 2 <= currentMaxHp)
            scale++;
        // Посмотреть текущие хп
        int newValue = calcValue();
        if (newValue * 10 <= currentMaxHp) {
            scale -= 2; // Если 10% и меньше хп, очень нужна помощь
        } else if (newValue * 2 <= currentMaxHp) {
            scale--; // Если 50% и меньше хп, слегка нужна помощь
        }
        // Если просел на треть относительно старта уровня, тоже нужна помощь
        boolean changes = Math.abs(newValue - startValue) > (startValue / 3);
        if (newValue < startValue && changes)
            scale--;
        if (scale <= -3)
            return Math.max(prevDifficulty - 1, -2);

        if (newValue >= startValue && newValue > 125)
            scale++;
        if (newValue > startValue && newValue > 70 && changes)
            scale++;

        if (scale > 0) {
            return Math.min(prevDifficulty + 1, 2);
        } else if (scale < 0) {
            return Math.max(prevDifficulty - 1, -2);
        }
        return 0;
    }
}
