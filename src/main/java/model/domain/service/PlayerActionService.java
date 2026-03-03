package model.domain.service;

import model.domain.AccessLevel;
import model.domain.Coordinates;
import model.domain.Entity;
import model.domain.LevelManager;
import model.domain.RunStats;
import model.domain.creature.combat.CombatResult;
import model.domain.creature.combat.CombatService;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.player.Hero;
import model.domain.events.CombatEvent;
import model.domain.events.ItemEvent;
import model.domain.events.SystemEvent;
import model.domain.geometry.Door;
import model.domain.geometry.Exit;
import model.domain.items.Item;
import model.domain.items.Key;
import model.domain.items.Treasure;

import model.domain.creature.enemy.Vampire;

public class PlayerActionService {
    private final Hero player;
    private final LevelManager levelManager;
    private final EnemyActionService enemyActionService;
    private final RunStats runStats;

    public PlayerActionService(Hero player, LevelManager levelManager, EnemyActionService enemyActionService, RunStats runStats) {
        this.player = player;
        this.levelManager = levelManager;
        this.enemyActionService = enemyActionService;
        this.runStats = runStats;
    }

    public ActionResult move(int deltaX, int deltaY) {
        if (player.isSleep()) {
            player.setSleep(false);
            return ActionResult.success();
        }
        Coordinates currentCoor = player.getCoor();
        Coordinates newCoor = new Coordinates(currentCoor.y() + deltaY, currentCoor.x() + deltaX);

        if (!isValidMove(newCoor)) return ActionResult.failure();

        Entity target = levelManager.getEntityAt(newCoor);
        ActionResult result;

        if (target instanceof Door door) {
            if (interactWithDoor(door)) {
                if (levelManager.moveEntity(player, newCoor)) {
                    runStats.incrementCellsTraveled();
                }
                result = ActionResult.success();
            } else {
                result = ActionResult.failure(new SystemEvent("Door is locked!"));
            }
        } else if (target instanceof Enemy enemy) {
            result = attack(enemy);
        } else if (target instanceof Item item) {
            result = interactWithItem(item);
            if (result.isSuccess() && levelManager.moveEntity(player, newCoor)) {
                runStats.incrementCellsTraveled();
            }
        } else if (target instanceof Exit) {
            int levelNumber = levelManager.getGameState().numberOfLevel();
            result = ActionResult.success(new SystemEvent("LEVEL_CLEARED:" + levelNumber));
        } else {
            if (levelManager.moveEntity(player, newCoor)) {
                runStats.incrementCellsTraveled();
                result = ActionResult.success();
            } else {
                result = ActionResult.failure();
            }
        }

        return result;
    }

    private ActionResult attack(Enemy enemy) {
        CombatResult result = CombatService.calculateAttack(player, enemy);

        if (enemy instanceof Vampire vampire && vampire.isFirstAttack()) {
            result = new CombatResult(0, false, false);
            System.out.println("First attack against vampire always misses");
            vampire.setFirstAttack(false);
        }

        if (result.didHit()) {
            runStats.incrementHitsDealt();
            System.out.println("Hero attack " + enemy.getType() + " and do " + result.damage() + " damage");
            System.out.println(enemy.getType() + " have " + enemy.getHealth() + " HP");

            enemy.setHealth(enemy.getHealth() - result.damage());
            if (result.defenderDied()) {
                enemyActionService.handleEnemyDeath(enemy);
            }
        }

        CombatEvent combatEvent = new CombatEvent(
                result,
                player.getName(),
                enemy.getType().name(),
                true
        );

        return ActionResult.success(combatEvent);
    }

    private ActionResult interactWithItem(Item item) {
        if (item instanceof Key key) {
            boolean[] keys = player.getKeys();
            switch (key.getAccess()) {
                case BLUE -> keys[0] = true;
                case YELLOW -> keys[1] = true;
                case RED -> keys[2] = true;
            }
            levelManager.removeEntity(item);
            return ActionResult.success(new ItemEvent(item.getName(), ItemEvent.ItemAction.PICKED_UP));
        } else if (item instanceof Treasure treasure) {
            int value = treasure.getValue();
            player.addValue(value);
            runStats.incrementTreasuresCollected(value);
            levelManager.removeEntity(treasure);
            return ActionResult.success(new ItemEvent(item.getName(), ItemEvent.ItemAction.PICKED_UP));
        } else {
            if (player.addItemToBackpack(item)) {
                levelManager.removeEntity(item);
                return ActionResult.success(new ItemEvent(item.getName(), ItemEvent.ItemAction.PICKED_UP));
            } else {
                levelManager.swapEntities(player.getCoor(), item.getCoor());
                return ActionResult.failure(new SystemEvent("Backpack full! Cannot pick up: " + item.getName()));
            }
        }
    }

    private boolean interactWithDoor(Door door) {
        AccessLevel accessLevel = door.getAccessLevel();
        boolean[] keys = player.getKeys();
        int access;
        switch (accessLevel) {
            case YELLOW -> access = 1;
            case RED -> access = 2;
            default -> access = 0;
        }
        if (keys[access]) {
            levelManager.removeEntity(door);
            return true;
        }
        return false;
    }

    private boolean isValidMove(Coordinates newCoor) {
        int[][] field = levelManager.getGameState().field();
        int x = newCoor.x();
        int y = newCoor.y();

        if (x < 0 || x >= field[0].length || y < 0 || y >= field.length) return false;
        int cell = field[y][x];
        return cell != 3;
    }
}
