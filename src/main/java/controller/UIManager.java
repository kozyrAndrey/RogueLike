package controller;

import controller.ui.UIState;
import view.presentation.dto.ItemDisplayDTO;

import java.util.List;

public class UIManager {
    private UIState currentState = UIState.normal();

    public UIState getCurrentState() { return currentState; }

    public void openItemSelection(String selectionType, String prompt, List<ItemDisplayDTO> items) {
        this.currentState = UIState.withSelection(selectionType, prompt, items);
    }

    public void closeItemSelection() {
        this.currentState = UIState.normal();
    }

    public void setNormalState() {
        this.currentState = UIState.normal();
    }

    public boolean isShowingItemSelection() {
        return currentState.showItemSelection();
    }

    public void reset() {
        this.currentState = UIState.normal();
    }
}
