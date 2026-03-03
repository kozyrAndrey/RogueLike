package controller;

import model.RogueModel;
import model.domain.GameState;
import controller.ui.UIState;
import model.domain.items.Item;
import model.domain.items.ItemType;
import model.domain.service.ActionResult;
import view.presentation.dto.ItemDisplayDTO;
import view.presentation.RoguePresenter;
import view.presentation.dto.GamePresentation;

import java.util.List;
import java.util.stream.Collectors;

import static controller.MenuManager.*;

public class RogueController {
    private static final String BACKPACK_PROMPT =
            "Backpack: h-weapons, j-food, k-elixirs, e-scrolls. b/esc-close";

    private final RogueModel model;
    private final RoguePresenter presenter;
    private boolean working = true;

    private final AppStateManager appState;
    private final MenuManager menuManager;
    private final PlayerSession playerSession;
    private final UIManager uiManager;

    public RogueController(RogueModel model) {
        this.model = model;
        this.presenter = new RoguePresenter();
        this.appState = new AppStateManager();
        this.menuManager = new MenuManager();
        updateContinueMenuItem();
        this.playerSession = new PlayerSession();
        this.uiManager = new UIManager();
    }

    private void updateContinueMenuItem() {
        boolean hasSave = model.hasSavedSession();
        menuManager.setMenuItemEnabled(CONTINUE_INDEX, hasSave);
    }

    public void handleCommand(Command command) {
        switch (appState.getCurrentState()) {
            case START_MENU ->  handleMenuCommand(command);
            case NAME_INPUT -> handleNameInputCommand(command);
            case GAME -> handleGameCommand(command);
            case GAME_OVER -> handleGameOverCommand();
            case STATS -> handleStatsCommand(command);
        }
    }

    private void handleMenuCommand(Command command) {
        switch (command.getType()) {
            case MENU_UP, MOVE_UP -> menuManager.moveUp();
            case MENU_DOWN, MOVE_DOWN -> menuManager.moveDown();
            case MENU_SELECT -> handleMenuSelection();
            case QUIT_GAME -> working = false;
            default -> {}
        }
    }

    private void handleMenuSelection() {
        MenuItem selected = menuManager.getSelectedItem();
        if (selected != null && selected.isEnabled()) {
            switch (menuManager.getSelectedIndex()) {
                case NEW_GAME_INDEX -> {
                    appState.setState(AppState.NAME_INPUT);
                    playerSession.reset();
                    uiManager.reset();
                }
                case CONTINUE_INDEX -> {
                    if (model.hasSavedSession() && model.loadLastSession()) {
                            appState.setState(AppState.GAME);
                            uiManager.setNormalState();
                    } else {
                        System.out.println("Unable to load save");
                        updateContinueMenuItem();
                    }
                }
                case STATISTICS_INDEX -> {
                    appState.setState(AppState.STATS);
                    uiManager.setNormalState();
                }
            }
        }
    }

    private void handleNameInputCommand(Command command) {
        if (command.hasCharacter()) {
            playerSession.appendCharacter(command.getCharacter());
            return;
        }

        switch (command.getType()) {
            case INPUT_BACKSPACE -> playerSession.deleteLastCharacter();
            case MENU_SELECT -> {
                if (playerSession.isNameValid()) {
                    model.startNewGame(playerSession.getPlayerName());
                    appState.setState(AppState.GAME);
                    uiManager.setNormalState();
                    updateContinueMenuItem();
                }
            }
            case MENU_BACK -> {
                appState.setState(AppState.START_MENU);
                updateContinueMenuItem();
            }
            default -> {}
        }
    }

    private void handleGameCommand(Command command) {
        if (uiManager.isShowingItemSelection()) {
            UIState uiState = uiManager.getCurrentState();
            if (UIState.BACKPACK_ROOT.equals(uiState.selectionType())) {
                handleBackpackRootCommand(command);
            } else {
                handleItemSelectionCommand(command, uiState);
            }
            return;
        }

        switch (command.getType()) {
            case MOVE_UP -> model.movePlayer(0, -1);
            case MOVE_RIGHT -> model.movePlayer(1, 0);
            case MOVE_DOWN -> model.movePlayer(0, 1);
            case MOVE_LEFT -> model.movePlayer(-1, 0);
            case OPEN_BACKPACK_MENU -> openBackpackRoot();
            case QUIT_GAME -> {
                working = false;
                model.saveCurrentSession();
            }
            case MENU_BACK -> {
                model.saveCurrentSession();
                appState.setState(AppState.START_MENU);
                uiManager.setNormalState();
                updateContinueMenuItem();
            }
            default -> {}
        }
        if (isGameOver()) {
            appState.setState(AppState.GAME_OVER);
            uiManager.setNormalState();
        }
    }

    void handleStatsCommand(Command command) {
        switch (command.getType()) {
            case MENU_BACK, QUIT_GAME -> {
                appState.setState(AppState.START_MENU);
                uiManager.setNormalState();
                updateContinueMenuItem();
            }
            default -> {}
        }
    }

    private void openItemSelection(ItemType itemType, String prompt) {
        List<Item> items = model.getItemsForSelection(itemType);
        List<ItemDisplayDTO> itemDTOs = presenter.presentItems(items);
        String selectionType = convertToSelectionType(itemType);
        uiManager.openItemSelection(selectionType, prompt, itemDTOs);
    }

    private String convertToSelectionType(ItemType itemType) {
        return switch (itemType) {
            case FOOD -> "FOOD";
            case ELIXIR -> "ELIXIR";
            case SCROLL -> "SCROLL";
            case WEAPON -> "WEAPON";
            default -> "NONE";
        };
    }

    private void openBackpackRoot() {
        uiManager.openItemSelection(UIState.BACKPACK_ROOT, BACKPACK_PROMPT, List.of());
    }

    private void handleBackpackRootCommand(Command command) {
        switch (command.getType()) {
            case OPEN_WEAPON_MENU -> openItemSelection(ItemType.WEAPON, "Select weapon (0 - unequip, 1-9 equip):");
            case OPEN_FOOD_MENU -> openItemSelection(ItemType.FOOD, "Select food to consume (1-9):");
            case OPEN_ELIXIR_MENU -> openItemSelection(ItemType.ELIXIR, "Select elixir to use (1-9):");
            case OPEN_SCROLL_MENU -> openItemSelection(ItemType.SCROLL, "Select scroll to use (1-9):");
            case MENU_BACK, OPEN_BACKPACK_MENU -> uiManager.closeItemSelection();
            default -> {
            }
        }
    }

    private void handleItemSelectionCommand(Command command, UIState uiState) {
        switch (command.getType()) {
            case UNEQUIP_WEAPON -> {
                if ("WEAPON".equals(uiState.selectionType())) {
                    ActionResult result = model.unequipWeapon();
                    model.processActionResult(result);
                    uiManager.closeItemSelection();
                }
            }
            case SELECT_ITEM_1 -> useSelectedItem(1, uiState);
            case SELECT_ITEM_2 -> useSelectedItem(2, uiState);
            case SELECT_ITEM_3 -> useSelectedItem(3, uiState);
            case SELECT_ITEM_4 -> useSelectedItem(4, uiState);
            case SELECT_ITEM_5 -> useSelectedItem(5, uiState);
            case SELECT_ITEM_6 -> useSelectedItem(6, uiState);
            case SELECT_ITEM_7 -> useSelectedItem(7, uiState);
            case SELECT_ITEM_8 -> useSelectedItem(8, uiState);
            case SELECT_ITEM_9 -> useSelectedItem(9, uiState);
            default -> uiManager.closeItemSelection();
        }
    }

    private void useSelectedItem(int index, UIState uiState) {
        Item selectedItem = model.getItemByIndex(index - 1);
        if (selectedItem != null) {
            String selectionType = uiState.selectionType();
            ActionResult result = switch (selectionType) {
                case "FOOD" -> model.useFood(selectedItem);
                case "ELIXIR" -> model.useElixir(selectedItem);
                case "SCROLL" -> model.useScroll(selectedItem);
                case "WEAPON" -> model.equipWeapon(selectedItem);
                default -> ActionResult.failure();
            };
            model.processActionResult(result);
        }
        uiManager.closeItemSelection();
    }

    private void handleGameOverCommand() {
        appState.setState(AppState.START_MENU);
        uiManager.setNormalState();
        updateContinueMenuItem();
    }

    public GamePresentation getGamePresentation() {
        GameState gameState = model.returnState();
        UIState uiState = uiManager.getCurrentState();
        return presenter.present(gameState, uiState);
    }

    public boolean isWorking() { return  working; }

    public boolean isGameOver() { return model.isGameOver(); }

    public AppState getCurrentAppState() { return appState.getCurrentState(); }

    public int getMenuSelection() { return menuManager.getSelectedIndex(); }

    public String getPlayerName() { return playerSession.getPlayerName(); }

    public List<String> getMenuLabels() {
        return menuManager.getMenuItems().stream()
                .map(MenuItem::getLabel)
                .collect(Collectors.toList());
    }

    public List<Boolean> getMenuEnabledStates() {
        return menuManager.getMenuItems().stream()
                .map(MenuItem::isEnabled)
                .collect(Collectors.toList());
    }

    public List<String> getAttemptLinesForStatsScreen() {
        return presenter.presentAttempts(model.getAttempts());
    }
}
