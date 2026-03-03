package view.presentation.dto;

import com.googlecode.lanterna.TextColor;

import java.util.List;

public record GamePresentation(
        FieldPresentation field,
        PlayerPresentation player,
        UIPresentation ui,
        List<String> logMessages
) {
    public static GamePresentation empty() {
        return new GamePresentation(
                new FieldPresentation(new char[0][0], new TextColor[0][0], 0, 0),
                PlayerPresentation.empty(),
                UIPresentation.normal(),
                List.of()
        );
    }
}
