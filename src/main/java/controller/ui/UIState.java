package controller.ui;

import view.presentation.dto.ItemDisplayDTO;

import java.util.List;

public class UIState {
    public static final String BACKPACK_ROOT = "BACKPACK_ROOT";

    private final SelectionStateDTO selectionState;

    public UIState(SelectionStateDTO selectionState) {
        this.selectionState = selectionState;
    }

    public static UIState normal() {
        return new UIState(new SelectionStateDTO(false, "NONE", "", List.of()));
    }

    public static UIState withSelection(String selectionType, String prompt, List<ItemDisplayDTO> items) {
        return new UIState(new SelectionStateDTO(true, selectionType, prompt, items));
    }

    public boolean showItemSelection() { return selectionState.showSelection(); }
    public String selectionType() { return selectionState.selectionType(); }
    public String selectionPrompt() { return selectionState.prompt(); }
    public List<ItemDisplayDTO> availableItems() { return selectionState.availableItems(); }
    public boolean isNormal() { return !showItemSelection(); }
}
