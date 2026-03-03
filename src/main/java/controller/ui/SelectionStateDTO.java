package controller.ui;

import view.presentation.dto.ItemDisplayDTO;

import java.util.List;

public record SelectionStateDTO(
        boolean showSelection,
        String selectionType,
        String prompt,
        List<ItemDisplayDTO> availableItems
) {
    public boolean hasItems() {
        return availableItems != null && !availableItems.isEmpty();
    }

    public int itemCount() {
        return hasItems() ? availableItems.size() : 0;
    }
}
