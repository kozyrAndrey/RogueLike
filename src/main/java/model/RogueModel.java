package model;

import datalayer.GameDataRepository;
import datalayer.dto.AttemptStatsDTO;
import datalayer.dto.LevelStateDTO;
import datalayer.dto.SavedSessionDTO;
import datalayer.snapshots.RunStatsSnapshot;
import model.domain.*;
import model.domain.creature.player.Hero;
import model.domain.events.GameEvent;
import model.domain.events.ItemEvent;
import model.domain.events.SystemEvent;
import model.domain.geometry.Level;
import model.domain.items.*;
import model.domain.service.ActionResult;
import model.domain.service.TurnSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static datalayer.SessionMapper.fromLevelSnapshot;
import static datalayer.SessionMapper.toLevelState;

public class RogueModel {
    private Hero player;
    private LevelManager lm;
    private TurnSystem turnSystem;
    private RunStats runStats;
    private final GameLog gameLog = new GameLog(50, 10);
    private final GameDataRepository gameDataRepository;
    private List<Item> lastSelectedItems = new ArrayList<>();
    private boolean gameOver = false;
    private GameState lastGameState;

    public RogueModel(GameDataRepository gdr) {
        this.gameDataRepository = gdr;
    }

    public void startNewGame(String playerName) {
        gameOver = false;
        if (gameDataRepository != null) {
            gameDataRepository.clearLastSession();
        }
        runStats = new RunStats();
        player = new Hero(new Coordinates(0, 0), 100, 20, 20, 100, playerName, 0);
        lm = new Level(1, player, 0).getLevelManager();
        this.turnSystem = new TurnSystem(lm, runStats);
        this.clearLog();
        addLogMessage(new SystemEvent("Welcome to the dungeon, " + playerName + "!").toLogMessage());
    }

    public boolean isGameOver() {
        return gameOver || (player != null && player.getHealth() <= 0);
    }

    public GameState returnState() {
        if (gameOver && lastGameState != null) return lastGameState;
        if (lm == null || player == null) {
            return new GameState(
                    new int[20][80],
                    new FogState[20][80], 1, 100, 100, 20, 20, 0,
                    new boolean[3], "Player", getLogMessages());
        }

        GameState levelState = lm.getGameState();
        return new GameState(levelState.field(), levelState.fog(), levelState.numberOfLevel(),
                levelState.maxHp(), levelState.curHp(),
                levelState.str(), levelState.dex(),
                levelState.score(), levelState.keys(),
                levelState.name(), getLogMessages());
    }

    public boolean movePlayer(int deltaX, int deltaY) {
        if (isGameOver() || lm == null || player == null) return false;

        boolean moved = turnSystem.executePlayerTurn(deltaX, deltaY);
        for (GameEvent event : turnSystem.getPendingEvents()) {

            if (event instanceof SystemEvent systemEvent) {
                String message = systemEvent.toLogMessage();

                if (message.startsWith("GAME_OVER:")) {
                    handlePlayerDeath();
                    continue;
                }

                if (message.startsWith("LEVEL_CLEARED:")) {
                    String levelStr = message.substring("LEVEL_CLEARED:".length());
                    int levelNumber;
                    try {
                        levelNumber = Integer.parseInt(levelStr);
                    } catch (NumberFormatException e) {
                        levelNumber = lm != null ? lm.getGameState().numberOfLevel() : 1;
                    }
                    handleLevelCleared(levelNumber);
                    continue;
                }
            }

            addLogMessage(event.toLogMessage());
        }
        turnSystem.clearPendingEvents();

        if (moved) {
            player.timeHasPassed();
            if (!gameOver) {
                this.lastGameState = lm.getGameState();
            }
        }
        return moved;
    }

    private void handlePlayerDeath() {
        if (gameOver) return;
        gameOver = true;
        int level = (lm != null) ? lm.getNumberOfLevel() : 1;
        if (lm != null) this.lastGameState = lm.getGameState();
        AttemptStatsDTO attemptStats = new AttemptStatsDTO(
                player.getName(),
                runStats.getTreasuresCollected(),
                level,
                runStats.getEnemiesDefeated(),
                runStats.getFoodEaten(),
                runStats.getElixirsUsed(),
                runStats.getScrollsRead(),
                runStats.getHitsDealt(),
                runStats.getHitsTaken(),
                runStats.getCellsTraveled(),
                false
        );
        gameDataRepository.appendAttempt(attemptStats);
        gameDataRepository.clearLastSession();
        addLogMessage(new SystemEvent("Game Over! Press any key to return to main menu.").toLogMessage());
    }

    private void handleLevelCleared(int completedLevel) {
        if (lm == null || player == null) return;

        int nextLevel = completedLevel + 1;

        if (nextLevel > 21) {
            gameOver = true;
            lastGameState = lm.getGameState();
            AttemptStatsDTO attemptStats = new AttemptStatsDTO(
                    player.getName(),
                    runStats.getTreasuresCollected(),
                    lastGameState.numberOfLevel(),
                    runStats.getEnemiesDefeated(),
                    runStats.getFoodEaten(),
                    runStats.getElixirsUsed(),
                    runStats.getScrollsRead(),
                    runStats.getHitsDealt(),
                    runStats.getHitsTaken(),
                    runStats.getCellsTraveled(),
                    true
            );
            gameDataRepository.appendAttempt(attemptStats);
            gameDataRepository.clearLastSession();
            addLogMessage(new SystemEvent(
                    "You have cleared all 21 levels! Press any key to return to main menu."
            ).toLogMessage());
            return;
        }

        player.setKeys(new boolean[3]);

        Level level = new Level(nextLevel, player, lm.getDifficultyForNextLevel());
        lm = level.getLevelManager();
        turnSystem = new TurnSystem(lm, runStats);

        lastGameState = lm.getGameState();
        makeSavedSession();
        addLogMessage(new SystemEvent(
                String.format("You descend to level %02d.", nextLevel)
        ).toLogMessage());
    }

    private void makeSavedSession() {
        if (lm == null || player == null || gameDataRepository == null) {
            return;
        }
        int completedLevel = Math.max(0, lm.getNumberOfLevel() - 1);
        RunStatsSnapshot statsSnapshot = new RunStatsSnapshot(
                runStats.getTreasuresCollected(),
                runStats.getEnemiesDefeated(),
                runStats.getFoodEaten(),
                runStats.getElixirsUsed(),
                runStats.getScrollsRead(),
                runStats.getHitsDealt(),
                runStats.getHitsTaken(),
                runStats.getCellsTraveled()
        );
        LevelStateDTO levelState = toLevelState(lm, player);
        SavedSessionDTO savedSession = new SavedSessionDTO(System.currentTimeMillis(), levelState, statsSnapshot, completedLevel);
        gameDataRepository.saveLastSession(savedSession);
    }

    public List<Item> getItemsForSelection(ItemType itemType) {
        List<Item> items = getItemsByType(itemType);

        this.lastSelectedItems = items;

        return items;
    }

    public Item getItemByIndex(int index) {
        if (index >= 0 && index < lastSelectedItems.size()) {
            return lastSelectedItems.get(index);
        }
        return null;
    }

    public List<Item> getItemsByType(ItemType itemType) {
        if (player != null) return player.getItemsByType(itemType);
        return new ArrayList<>();
    }

    public ActionResult equipWeapon(Item weapon) {
        if (weapon instanceof Weapon) {
            List<GameEvent> events = new ArrayList<>();
            if (player.getEquippedWeapon() != null) {
                Weapon droppedWeapon = player.getEquippedWeapon();
                unequipWeapon();
                events.add(new ItemEvent(droppedWeapon.getName(), ItemEvent.ItemAction.UNEQUIPPED));
            }
            player.equipWeapon((Weapon) weapon);
            player.removeItemFromBackpack(weapon);
            events.add(new ItemEvent(
                    weapon.getName(), ItemEvent.ItemAction.EQUIPPED
            ));
            return ActionResult.success(events);
        }
        return ActionResult.failure();
    }

    public ActionResult unequipWeapon() {
        Weapon currentWeapon = player.getEquippedWeapon();
        if (currentWeapon != null) {
            Weapon droppedWeapon = player.unequipWeapon();
            lm.placeItemNear(droppedWeapon, player.getCoor());
            return ActionResult.success(new ItemEvent(
                    currentWeapon.getName(), ItemEvent.ItemAction.UNEQUIPPED
            ));
        }
        return ActionResult.failure();
    }


    public ActionResult useFood(Item food) {
        if (food instanceof Food realFood) {
            List<GameEvent> events = new ArrayList<>();
            int heal = realFood.getValue();
            int playerHP = player.getHealth() + heal;
            events.add(new ItemEvent(food.getName(), ItemEvent.ItemAction.USED));
            runStats.incrementFoodEaten();
            player.removeItemFromBackpack(food);
            player.setHealth(playerHP);
            events.add(new SystemEvent(String.format("You healed %d health", heal)));
            return ActionResult.success(events);
        }
        return ActionResult.failure();
    }

    public ActionResult useElixir(Item elixir) {
        if (elixir instanceof Elixir realElixir) {
            List<GameEvent> events = new ArrayList<>();
            int boost = realElixir.getValue();
            String playerStat;
            events.add(new ItemEvent(elixir.getName(), ItemEvent.ItemAction.USED));
            runStats.incrementElixirsUsed();
            switch (realElixir.getBoostType()) {
                case HEALTH -> {
                    player.addBuff(BoostType.HEALTH, boost, 60);
                    player.setMaxHealth(player.getMaxHealth() + boost);
                    player.setHealth(player.getHealth() + boost);
                    playerStat = "health";
                }
                case STRENGTH -> {
                    player.addBuff(BoostType.STRENGTH, boost, 60);
                    player.setStrength(player.getBaseStrength() + boost);
                    playerStat = "strength";
                }
                case DEXTERITY -> {
                    player.addBuff(BoostType.DEXTERITY, boost, 60);
                    player.setDexterity(player.getDexterity() + boost);
                    playerStat = "dexterity";
                }
                default -> playerStat = "";
            }
            events.add(new SystemEvent(String.format("You temporary increased your %s by %d", playerStat, boost)));
            player.removeItemFromBackpack(elixir);
            return ActionResult.success(events);
        }
        return ActionResult.failure();
    }

    public ActionResult useScroll(Item scroll) {
        if (scroll instanceof Scroll realScroll) {
            List<GameEvent> events = new ArrayList<>();
            int boost = realScroll.getValue();
            String playerStat;
            events.add(new ItemEvent(scroll.getName(), ItemEvent.ItemAction.USED));
            runStats.incrementScrollsRead();
            switch (realScroll.getBoostType()) {
                case HEALTH -> {
                    player.setMaxHealth(player.getMaxHealth() + boost);
                    player.setHealth(player.getHealth() + boost);
                    playerStat = "health";
                }
                case STRENGTH -> {
                    player.setStrength(player.getBaseStrength() + boost);
                    playerStat = "strength";
                }
                case DEXTERITY -> {
                    player.setDexterity(player.getDexterity() + boost);
                    playerStat = "dexterity";
                }
                default -> playerStat = "";
            }
            events.add(new SystemEvent(String.format("You increased your %s by %d", playerStat, boost)));
            player.removeItemFromBackpack(scroll);
            return ActionResult.success(events);
        }
        return ActionResult.failure();
    }

    public void addLogMessage(String message) {
        gameLog.addMessage(message);
    }

    public List<String> getLogMessages() {
        return gameLog.getVisibleMessages();
    }

    public void clearLog() {
        gameLog.clear();
    }

    public void processActionResult(ActionResult result) {
        for (GameEvent event : result.getEvents()) addLogMessage(event.toLogMessage());
    }

    public boolean hasSavedSession() {
        return gameDataRepository.hasLastSession();
    }

    public boolean loadLastSession() {
        Optional<SavedSessionDTO> opt = gameDataRepository.loadLastSession();
        if (opt.isEmpty()) {
            return false;
        }

        try {
            return restoreSession(opt.get());
        } catch (RuntimeException e) {
            gameDataRepository.clearLastSession();
            return false;
        }
    }

    private boolean restoreSession(SavedSessionDTO session) {
        LevelManager lm = fromLevelSnapshot(session.currentLevelState());
        this.lm = lm;
        this.player = lm.getPlayer();
        if (session.runStats() != null) {
            RunStatsSnapshot s = session.runStats();
            this.runStats = new RunStats(
                    s.treasuresCollected(),
                    s.enemiesDefeated(),
                    s.foodEaten(),
                    s.elixirsUsed(),
                    s.scrollsRead(),
                    s.hitsDealt(),
                    s.hitsTaken(),
                    s.cellsTraveled()
            );
        } else {
            this.runStats = new RunStats();
        }
        this.turnSystem = new TurnSystem(lm, runStats);
        this.gameOver = false;
        this.lastGameState = lm.getGameState();
        gameLog.clear();
        return true;
    }

    public List<AttemptStatsDTO> getAttempts() {
        return gameDataRepository.loadAllAttempts().stream()
                .sorted(Comparator.comparingInt(AttemptStatsDTO::treasuresCollected)
                        .thenComparingInt(AttemptStatsDTO::reachedLevel)
                        .reversed())
                .toList();
    }

    public void saveCurrentSession() {
        makeSavedSession();
    }
}
