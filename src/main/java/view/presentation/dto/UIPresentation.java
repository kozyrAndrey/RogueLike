package view.presentation.dto;

import java.util.List;

public record UIPresentation(
        boolean showSelection,
        String prompt,
        List<String> itemOptions,
        boolean showUnequipOption,
        String unequipOptionText
) {
    public static UIPresentation normal() {
        return new UIPresentation(false, "", List.of(), false, "");
    }

    public static UIPresentation withSelection(String prompt, List<String> itemOptions, boolean showUnequip, String unequipText) {
        return new UIPresentation(true, prompt, itemOptions, showUnequip, unequipText);
    }
}
