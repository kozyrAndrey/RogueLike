package model.domain.service;

import model.domain.Coordinates;
import model.domain.Entity;
import model.domain.LevelManager;
import model.domain.RunStats;
import model.domain.creature.combat.CombatResult;
import model.domain.creature.combat.CombatService;
import model.domain.creature.enemy.*;
import model.domain.creature.player.Hero;
import model.domain.events.CombatEvent;
import model.domain.events.GameEvent;
import model.domain.events.SystemEvent;
import model.domain.geometry.Exit;
import model.domain.items.Item;
import model.domain.items.Key;
import model.domain.items.Treasure;


import java.util.*;

public class EnemyActionService {
    private final LevelManager levelManager;
    private final RunStats runStats;
    private final Random rng = new Random();

    public EnemyActionService(LevelManager levelManager, RunStats runStats) {
        this.levelManager = levelManager;
        this.runStats = runStats;
    }

    public List<GameEvent> executeEnemyTurn(Enemy enemy, Hero player) {
        List<GameEvent> events = new ArrayList<>();
        if (shouldChase(enemy, player)) {
            events.addAll(chase(enemy, player));
            if(enemy.getType() == EnemyType.MIMIC) {
                Mimic mimic = (Mimic) enemy;
                mimic.setMimicHiding(false);
            }
            if(enemy.getType() == EnemyType.GHOST) {
                Ghost ghost = (Ghost) enemy;
                ghost.setInvisible(false);
            }
        } else {
            if (enemy.getType() == EnemyType.OGRE) moveRandomly(enemy, 2);
            else if (enemy.getType() == EnemyType.GHOST) {
                moveRandomly(enemy, 17);
                Ghost ghost = (Ghost) enemy;
                if (rng.nextInt(2) == 0) ghost.setInvisible(true);
                else ghost.setInvisible(false);
            } else if (enemy.getType() == EnemyType.MIMIC) {
                Mimic mimic = (Mimic) enemy;
                mimic.setMimicHiding(true);
            } else moveRandomly(enemy, 1);
        }
        return events;
    }

    private boolean shouldChase(Enemy enemy, Hero player) {
        int distance = calculateDistance(enemy.getCoor(), player.getCoor());
        return distance <= enemy.getHostility();
    }

    private int calculateDistance(Coordinates a, Coordinates b) {
        return Math.abs(a.y() - b.y()) + Math.abs(a.x() - b.x());
    }

    private List<GameEvent> chase(Enemy enemy, Hero player) {
//        System.out.print(enemy.getType() + " try to chase hero");
        Coordinates enemyCoor = enemy.getCoor();
        ArrayList<Coordinates> movesTowardPlayer = new ArrayList<>();

        if (enemy.getType() == EnemyType.SNAKE_MAGE) calculateSnakeMoves(enemy, player, movesTowardPlayer);
        else calculateMoves(enemy, player, movesTowardPlayer);


        for (int move = 0; move < movesTowardPlayer.size(); move++) {
            if (canMoveTo(movesTowardPlayer.get(move))) {
//                System.out.println("and do this");
                Entity target = levelManager.getEntityAt(movesTowardPlayer.get(move));
                if (target instanceof Hero) {
                    return attack(enemy, player);
                } else if (target == null) {
                    levelManager.moveEntity(enemy, movesTowardPlayer.get(move));
                    if (enemy.getType() == EnemyType.OGRE) moveOgre(enemy, player);
                    return Collections.emptyList();
                } else if (target instanceof Item){
                    levelManager.swapEntities(enemyCoor, movesTowardPlayer.get(move));
                    return Collections.emptyList();
                }
            }
        }
//        System.out.println("and cant do this");
        moveRandomly(enemy, 1);
//        if (enemy.getType() == EnemyType.OGRE) moveRandomly(enemy);
        return Collections.emptyList();
    }

    private List<GameEvent> attack(Enemy enemy, Hero player) {
        CombatResult result = CombatService.calculateAttack(enemy, player);
        List<GameEvent> events = new ArrayList<>();

        if (enemy instanceof Ogre ogre) {
            result = ogreAttack(result, ogre);
        }

        if (result.didHit()) {
            runStats.incrementHitsTaken();
            System.out.print(enemy.getType() + " attack hero ");
            player.setHealth(player.getHealth() - result.damage());
            System.out.println(result.damage());
            if(enemy.getType() == EnemyType.VAMPIRE){
                player.reduceMaxHealth(rng.nextInt(5));
                if(player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            } else if(enemy.getType() == EnemyType.SNAKE_MAGE){
                if(rng.nextInt(2) == 0) {
                    System.out.println("sleep");
                    player.setSleep(true);
                }
            }

            CombatEvent combatEvent = new CombatEvent(
                    result,
                    enemy.getType().name(),
                    player.getName(),
                    false
            );
            events.add(combatEvent);

            if (result.defenderDied()) {
                events.add(new SystemEvent("GAME_OVER:Killed by " + enemy.getType()));
            }
        } else {
            CombatEvent missEvent = new CombatEvent(
                    result,
                    enemy.getType().name(),
                    player.getName(),
                    false
            );
            events.add(missEvent);
        }
        return events;
    }
    private void moveRandomly(Enemy enemy, int steps) {
        for (int i = 0; i < steps; i++) {
            ArrayList<Coordinates> possibleMoves = getPossibleMoves(enemy.getCoor(), enemy);
            if (!possibleMoves.isEmpty()) {
                Coordinates move = possibleMoves.get(rng.nextInt(possibleMoves.size()));
                Entity entity = levelManager.getEntityAt(move);
                if (entity == null) {
                    levelManager.moveEntity(enemy, move);
                } else if (entity instanceof Item) {
                    levelManager.swapEntities(enemy.getCoor(), move);
                }
            }
        }
    }

    private ArrayList<Coordinates> getPossibleMoves(Coordinates currentCoor, Enemy enemy) {
        ArrayList<Coordinates> moves = new ArrayList<>();
        Coordinates[] directions;
        if (enemy.getType() == EnemyType.SNAKE_MAGE) {
            directions = new Coordinates[]{
                    new Coordinates(currentCoor.y() - 1, currentCoor.x() - 1),
                    new Coordinates(currentCoor.y() + 1, currentCoor.x() + 1),
                    new Coordinates(currentCoor.y() + 1, currentCoor.x() - 1),
                    new Coordinates(currentCoor.y() - 1, currentCoor.x() + 1)
            };
        } else {
            directions = new Coordinates[]{
                    new Coordinates(currentCoor.y() - 1, currentCoor.x()),
                    new Coordinates(currentCoor.y(), currentCoor.x() + 1),
                    new Coordinates(currentCoor.y() + 1, currentCoor.x()),
                    new Coordinates(currentCoor.y(), currentCoor.x() - 1)
            };
        }

        for (Coordinates newCoor : directions) {
            if (canMoveTo(newCoor)) moves.add(newCoor);
        }
        return moves;
    }

    private boolean canMoveTo(Coordinates coor) {
        if (!levelManager.isCellPassable(coor)) return false;

        Entity entity = levelManager.getEntityAt(coor);
        if (entity == null) return true;
        return !(entity instanceof Enemy || entity instanceof Exit || entity instanceof Key);
    }

    public void handleEnemyDeath(Enemy enemy) {
        int reward = enemy.getMaxHealth() + enemy.getStrength() + enemy.getDexterity();
        Coordinates coor = enemy.getCoor();
        levelManager.removeEntity(enemy);
        runStats.incrementEnemiesDefeated();
        levelManager.addEntity(new Treasure(coor, reward));
    }

    private void moveOgre(Enemy enemy, Hero player) {
        Coordinates playerCoor = player.getCoor();
        Coordinates enemyCoor = enemy.getCoor();
        int x = enemyCoor.x();
        int y = enemyCoor.y();
        if (playerCoor.y() < enemyCoor.y()) {
            y--;
        } else if (playerCoor.y() > enemyCoor.y()) {
            y++;
        }
        if (playerCoor.x() < enemyCoor.x()) {
            x--;
        } else if (playerCoor.x() > enemyCoor.x()) {
            x++;
        }
        Coordinates newCoor = new Coordinates(y, x);
        if (canMoveTo(newCoor)) levelManager.moveEntity(enemy, newCoor);
    }

    private void calculateMoves(Enemy enemy, Hero player, ArrayList<Coordinates> movesTowardPlayer){
        Coordinates playerCoor = player.getCoor();
        Coordinates enemyCoor = enemy.getCoor();
        if (playerCoor.y() < enemyCoor.y()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y() - 1, enemyCoor.x()));
        } else if (playerCoor.y() > enemyCoor.y()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y() + 1, enemyCoor.x()));
        }
        if (playerCoor.x() < enemyCoor.x()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y(), enemyCoor.x() - 1));
        } else if (playerCoor.x() > enemyCoor.x()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y(), enemyCoor.x() + 1));
        }
    }

    private void calculateSnakeMoves(Enemy enemy, Hero player, ArrayList<Coordinates> movesTowardPlayer){
        Coordinates playerCoor = player.getCoor();
        Coordinates enemyCoor = enemy.getCoor();
        if (playerCoor.y() < enemyCoor.y() && playerCoor.x() < enemyCoor.x()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y() - 1, enemyCoor.x() - 1));
        }
        if (playerCoor.y() < enemyCoor.y() && playerCoor.x() > enemyCoor.x()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y() - 1, enemyCoor.x() + 1));
        }
        if (playerCoor.y() > enemyCoor.y() && playerCoor.x() < enemyCoor.x()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y() + 1, enemyCoor.x() - 1));
        }
        if (playerCoor.y() > enemyCoor.y() && playerCoor.x() > enemyCoor.x()) {
            movesTowardPlayer.add(new Coordinates(enemyCoor.y() + 1, enemyCoor.x() + 1));
        }
        if(movesTowardPlayer.isEmpty()) calculateMoves(enemy, player, movesTowardPlayer);
    }

    private CombatResult ogreAttack(CombatResult result, Ogre ogre) {
        if (ogre.isCounterAttack()) {
            result = new CombatResult(result.damage(), true, result.defenderDied());
            System.out.println("Ogre tochno atakuet");
            ogre.setCounterAttack(false);
        } else if(!ogre.isRest()) {
            System.out.println("Ogre first attack");
            ogre.setRest(true);
        }
        else {
            result = new CombatResult(0, false, false);
            System.out.println("Ogre rests");
            ogre.setRest(false);
            ogre.setCounterAttack(true);
        }
        return result;
    }
}
