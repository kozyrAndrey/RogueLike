package view.presentation.dto;

import com.googlecode.lanterna.TextColor;

import java.util.List;

public record PlayerPresentation(
        String levelInfo,
        String healthBar,
        String stats,
        String goldInfo,
        List<KeyDisplay> keys
) {
    public record KeyDisplay(
            char symbol,
            TextColor color,
            boolean hasKey
    ) {}
    public static PlayerPresentation empty() {
        return new PlayerPresentation("", "", "", "", List.of());
    }
}
