package view.presentation.dto;

import com.googlecode.lanterna.TextColor;

public record FieldPresentation(
        char[][] symbols,
        TextColor[][] colors,
        int width,
        int height
) {
    public char getSymbolAt(int y, int x) {
        return symbols[y][x];
    }

    public TextColor getColorAt(int y, int x) {
        return colors[y][x];
    }
}
