package model.domain;

import model.domain.creature.enemy.*;
import model.domain.creature.player.Hero;
import model.domain.geometry.Door;
import model.domain.geometry.Exit;
import model.domain.items.*;
import model.domain.service.BalanceService;
import model.domain.service.FogOfWarService;

import java.util.*;

/**
 * Менеджер уровня игрового мира.
 *
 * Хранит карту уровня, список сущностей, игрока, врагов, предметы,
 * двери, выход и состояние тумана войны. Отвечает за добавление,
 * удаление и перемещение сущностей, проверку проходимости клеток
 * и формирование состояния уровня для отображения.
 *
 * Основные обязанности:
 * - хранение карты уровня;
 * - управление сущностями на уровне;
 * - обновление тумана войны;
 * - размещение предметов, врагов, дверей и выхода;
 * - формирование GameState для слоя представления.
 *
 * @see model.domain.GameState
 * @see model.domain.creature.player.Hero
 * @see model.domain.creature.enemy.Enemy
 */

public class LevelManager {
    public static final int MAP_HEIGHT = 20;
    public static final int MAP_WIDTH = 80;

    private FogState[][] fog;
    private final FogOfWarService fogService = new FogOfWarService();
    private final HashMap<Coordinates, Entity> entityMap;

    private Hero player;
    private final ArrayList<Enemy> enemies;
    private final ArrayList<Item> items;
    private final ArrayList<Door> doors;
    private int[][] field;
    private final int numberOfLevel;
    private final BalanceService balanceService;

    public LevelManager(int number, Hero player, int difficulty) {
        this.entityMap = new HashMap<>(MAP_HEIGHT * MAP_WIDTH, 1.0f);
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.player = player;
        this.numberOfLevel = number;
        this.balanceService = new BalanceService(player, difficulty);
        this.fog = new FogState[MAP_HEIGHT][MAP_WIDTH];
        initializeFog();
    }

    public LevelManager(int number, Hero player, BalanceService balanceService) {
        this.entityMap = new HashMap<>(MAP_HEIGHT * MAP_WIDTH, 1.0f);
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.player = player;
        this.numberOfLevel = number;
        this.balanceService = balanceService;
        this.fog = new FogState[MAP_HEIGHT][MAP_WIDTH];
        initializeFog();
    }

    public int getBalancePrevDifficulty() {
        return balanceService.getPrevDifficulty();
    }

    public int getBalanceMaxHpOnStart() {
        return balanceService.getMaxHpOnStart();
    }

    public int getBalanceStartValue() {
        return balanceService.getStartValue();
    }    

    public int getDifficultyForNextLevel() {
        return balanceService.calcDifficultyForNextLevel();
    }

    private void initializeFog() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                fog[y][x] = FogState.HIDDEN;
            }
        }
    }

    public void updateFog() {
        if (player == null || field == null)
            return;
        if (fog == null || fog.length != field.length || fog[0].length != field[0].length) {
            fog = new FogState[field.length][field[0].length];
            initializeFog();
        }

        FogState[][] newVisibility = fogService.calculateVisibility(
                player.getCoor(),
                field);
        fog = fogService.updateFog(fog, newVisibility);
    }

    public Hero getPlayer() {
        return player;
    }

    public int[][] getFieldCopy() {
        int height = field.length;
        int width = field[0].length;
        int[][] copy = new int[height][width];
        for (int i = 0; i < height; i++) {
            copy[i] = Arrays.copyOf(field[i], width);
        }
        return copy;
    }

    public boolean[][] getExploredCopy() {
        if (fog == null)
            return new boolean[field.length][field[0].length];
        int height = fog.length;
        int width = fog[0].length;
        boolean[][] copy = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                copy[y][x] = fog[y][x] != FogState.HIDDEN;
            }
        }
        return copy;
    }

    public int getNumberOfLevel() {
        return numberOfLevel;
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public List<Door> getDoors() {
        return new ArrayList<>(doors);
    }

    public Exit getExit() {
        for (Entity entity : entityMap.values()) {
            if (entity instanceof Exit exit) {
                return exit;
            }
        }
        return null;
    }

    public void setExplored(boolean[][] explored) {
        if (explored == null)
            return;
        fog = new FogState[explored.length][explored[0].length];
        for (int y = 0; y < explored.length; y++) {
            for (int x = 0; x < explored[0].length; x++) {
                fog[y][x] = explored[y][x] ? FogState.EXPLORED : FogState.HIDDEN;
            }
        }
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    public void addEntity(Entity entity) {
        Coordinates coor = entity.getCoor();
        entityMap.put(coor, entity);

        if (entity instanceof Hero) {
            this.player = (Hero) entity;
        } else if (entity instanceof Enemy enemy) {
            enemies.add(enemy);
        } else if (entity instanceof Item item) {
            items.add(item);
        } else if (entity instanceof Door door) {
            doors.add(door);
        }
    }

    public void removeEntity(Entity entity) {
        Coordinates coor = entity.getCoor();
        entityMap.remove(coor);

        if (entity instanceof Hero) {
            this.player = null;
        } else if (entity instanceof Enemy enemy) {
            enemies.remove(enemy);
        } else if (entity instanceof Item item) {
            items.remove(item);
        } else if (entity instanceof Door door) {
            doors.remove(door);
        }
    }

    public boolean moveEntity(Entity entity, Coordinates newCoor) {
        if (entityMap.containsKey(newCoor) || entity instanceof Door)
            return false;

        Coordinates oldCoor = entity.getCoor();
        entityMap.remove(oldCoor);
        entity.setCoor(newCoor);
        entityMap.put(newCoor, entity);
        return true;
    }

    public void swapEntities(Coordinates coor1, Coordinates coor2) {
        Entity entity1 = entityMap.get(coor1);
        Entity entity2 = entityMap.get(coor2);
        entityMap.put(coor2, entityMap.put(coor1, entityMap.get(coor2)));
        entity1.setCoor(coor2);
        entity2.setCoor(coor1);
    }

    public boolean isCellOccupied(Coordinates coor) {
        return entityMap.containsKey(coor);
    }

    public boolean isCellPassable(Coordinates coor) {
        if (coor.x() < 0 || coor.x() >= MAP_WIDTH || coor.y() < 0 || coor.y() >= MAP_HEIGHT)
            return false;

        int cellValue = field[coor.y()][coor.x()];
        return !(cellValue >= 3 && cellValue <= 6);
    }

    public Entity getEntityAt(Coordinates coor) {
        return entityMap.get(coor);
    }

    public List<Entity> getAllEntities() {
        return new ArrayList<>(entityMap.values());
    }

    public void clearAll() {
        entityMap.clear();
        enemies.clear();
        items.clear();
        player = null;
    }
	/**
 * Формирует текущее состояние уровня для отображения.
 *
 * Метод обновляет туман войны, копирует карту уровня и размещает на ней
 * видимые сущности: игрока, врагов, предметы, двери и выход.
 *
 * @return объект GameState с актуальными данными уровня
 */
    public GameState getGameState() {
        updateFog();
        int[][] tmpField = new int[MAP_HEIGHT][MAP_WIDTH];
        FogState[][] tmpFog = new FogState[MAP_HEIGHT][MAP_WIDTH];
        for (int i = 0; i < MAP_HEIGHT; i++) {
            tmpField[i] = Arrays.copyOf(field[i], MAP_WIDTH);
            tmpFog[i] = Arrays.copyOf(fog[i], MAP_WIDTH);
        }
        for (Entity entity : getAllEntities()) {
            if (entity instanceof Item item) {
                placeItemOnField(item, tmpField);
            } else if (entity instanceof Door door) {
                placeDoorOnField(door, tmpField);
            } else if (entity instanceof Exit exit) {
                placeExitOnField(exit, tmpField);
            } else if (entity instanceof Enemy enemy) {
                placeEnemyOnField(enemy, tmpField);
            }
        }
        if (player != null) {
            tmpField[player.getCoor().y()][player.getCoor().x()] = 10;
        }
        return new GameState(
                tmpField,
                tmpFog,
                numberOfLevel,
                player != null ? player.getMaxHealth() : 0,
                player != null ? player.getHealth() : 0,
                player != null ? player.getStrength() : 0,
                player != null ? player.getDexterity() : 0,
                player != null ? player.getValue() : 0,
                player != null ? player.getKeys() : new boolean[3],
                player != null ? player.getName() : "",
                null);
    }

    public void setPlayerPosition(Coordinates coor) {
        this.player.setCoor(coor);
        addEntity(player);
    }

    public void placeItemOnField(Item item, int[][] field) {
        Coordinates coor = item.getCoor();
        FogState fogState = fog[coor.y()][coor.x()];
        if (fogState == FogState.VISIBLE) {
            if (item instanceof Key key) {
                switch (key.getAccess()) {
                    case BLUE -> field[coor.y()][coor.x()] = 7;
                    case YELLOW -> field[coor.y()][coor.x()] = 8;
                    case RED -> field[coor.y()][coor.x()] = 9;
                }
            }
            if (item instanceof Treasure)
                field[coor.y()][coor.x()] = 11;
            if (item instanceof Food)
                field[coor.y()][coor.x()] = 12;
            if (item instanceof Elixir)
                field[coor.y()][coor.x()] = 13;
            if (item instanceof Scroll)
                field[coor.y()][coor.x()] = 14;
            if (item instanceof Weapon)
                field[coor.y()][coor.x()] = 15;
        }
    }

    public void placeDoorOnField(Door door, int[][] field) {
        Coordinates coor = door.getCoor();
        FogState fogState = fog[coor.y()][coor.x()];
        if (fogState == FogState.VISIBLE) {
            AccessLevel access = door.getAccessLevel();
            switch (access) {
                case BLUE -> field[coor.y()][coor.x()] = 4;
                case YELLOW -> field[coor.y()][coor.x()] = 5;
                case RED -> field[coor.y()][coor.x()] = 6;
            }
        } else if (fogState == FogState.EXPLORED) {
            field[coor.y()][coor.x()] = 3;
        }
    }

    public void placeExitOnField(Exit exit, int[][] field) {
        Coordinates coor = exit.getCoor();
        FogState fogState = fog[coor.y()][coor.x()];
        if (fogState == FogState.VISIBLE) {
            field[coor.y()][coor.x()] = 40;
        }
    }

    public void placeEnemyOnField(Enemy enemy, int[][] field) {
        Coordinates coor = enemy.getCoor();
        FogState fogState = fog[coor.y()][coor.x()];
        if (fogState == FogState.VISIBLE) {
            if (enemy instanceof Zombie)
                field[coor.y()][coor.x()] = 20;
            if (enemy instanceof Vampire)
                field[coor.y()][coor.x()] = 21;
            if (enemy instanceof Ghost ghost && !ghost.isInvisible()) {
                field[coor.y()][coor.x()] = 22;
            }
            if (enemy instanceof Ogre)
                field[coor.y()][coor.x()] = 23;
            if (enemy instanceof SnakeMage)
                field[coor.y()][coor.x()] = 24;
            if (enemy instanceof Mimic mimic) {
                if (!mimic.isMimicHiding()) {
                    field[coor.y()][coor.x()] = 25;
                } else {
                    field[coor.y()][coor.x()] = mimic.getHidingSymbol();
                }
            }
        }
    }

    public boolean placeItemNear(Item item, Coordinates coor) {
        ArrayList<Coordinates> emptySpace = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;

                Coordinates newCoor = new Coordinates(coor.y() + y, coor.x() + x);

                if (isCellPassable(newCoor) && !isCellOccupied(newCoor)) {
                    emptySpace.add(newCoor);
                }
            }
        }
        if (!emptySpace.isEmpty()) {
            Collections.shuffle(emptySpace);
            Coordinates chosenCoor = emptySpace.get(0);
            item.setCoor(chosenCoor);
            addEntity(item);
            return true;
        }
        return false;
    }

}
