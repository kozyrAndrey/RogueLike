package model.domain.service;

import model.domain.LevelManager;
import model.domain.RunStats;
import model.domain.creature.enemy.Enemy;
import model.domain.creature.player.Hero;
import model.domain.events.GameEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TurnSystem {
    private final PlayerActionService playerActions;
    private final EnemyActionService enemyActions;
    private final LevelManager levelManager;
    private final List<GameEvent> pendingEvents = new ArrayList<>();

    public TurnSystem(LevelManager levelManager, RunStats runStats) {
        this.levelManager = levelManager;
        this.enemyActions = new EnemyActionService(levelManager, runStats);
        this.playerActions = new PlayerActionService(
                levelManager.getPlayer(),
                levelManager,
                enemyActions,
                runStats
        );
    }

    public boolean executePlayerTurn(int deltaX, int deltaY) {
        if (levelManager.getPlayer() == null) return false;

        pendingEvents.clear();
        ActionResult result = playerActions.move(deltaX, deltaY);
        if (result.isSuccess()) {
            pendingEvents.addAll(result.getEvents());
            if (levelManager.getPlayer() != null  &&
                    levelManager.getPlayer().getHealth() > 0) {
                executeEnemyTurns();
            }
        } else {
            pendingEvents.addAll(result.getEvents());
        }
        return result.isSuccess();
    }

    private void executeEnemyTurns() {
        Hero player = levelManager.getPlayer();
        if (player == null) return;

        ArrayList<Enemy> enemiesCopy = new ArrayList<>(levelManager.getEnemies());

        for (Enemy enemy : enemiesCopy) {
            if (levelManager.getEntityAt(enemy.getCoor()) == enemy) {
                List<GameEvent> enemyEvents = enemyActions.executeEnemyTurn(enemy, player);
                pendingEvents.addAll(enemyEvents);
            }
        }
    }

    public List<GameEvent> getPendingEvents() { return Collections.unmodifiableList(pendingEvents); }

    public void clearPendingEvents() { pendingEvents.clear(); }
}
