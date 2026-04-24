package controller;

import view.presentation.RogueView;
import view.presentation.dto.GamePresentation;

import java.io.IOException;
import java.util.List;

/**
 * Игровой цикл приложения.
 *
 * Организует непрерывную работу игры: получение ввода пользователя,
 * обработку команд, обновление состояния модели и перерисовку интерфейса.
 * Используется для управления жизненным циклом игровой сессии.
 */

public class RogueLoop {
    private final InputHandler inputHandler;
    private final RogueController controller;
    private final RogueView view;
    private boolean running = true;

    public RogueLoop(InputHandler inputHandler, RogueController controller, RogueView view) {
        this.inputHandler = inputHandler;
        this.controller = controller;
        this.view = view;
    }

    public void run() {
        try {
            renderCurrentView();
            while (running) {
                Command command = inputHandler.getNextCommand();
                controller.handleCommand(command);
                if (!controller.isWorking()) break;
                renderCurrentView();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderCurrentView() throws IOException {
        AppState currentState = controller.getCurrentAppState();

        switch (currentState) {
            case START_MENU -> {
                List<String> menuLabels = controller.getMenuLabels();
                List<Boolean> menuEnabledStated = controller.getMenuEnabledStates();
                int selectedIndex = controller.getMenuSelection();
                view.renderStartMenu(menuLabels, menuEnabledStated, selectedIndex);
            }
            case NAME_INPUT -> view.renderNameInput(controller.getPlayerName());
            case GAME -> {
                GamePresentation presentation = controller.getGamePresentation();
                view.renderGame(presentation);
            }
            case GAME_OVER -> view.renderGameOver();
            case STATS -> {
                List<String> currentAttempts = controller.getAttemptLinesForStatsScreen();
                view.renderStatistics(currentAttempts);
            }
        }
    }

    public void stop() { running = false; }
}
