package view.presentation;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import view.presentation.dto.FieldPresentation;
import view.presentation.dto.GamePresentation;
import view.presentation.dto.PlayerPresentation;
import view.presentation.dto.UIPresentation;

import java.io.IOException;
import java.util.List;

/**
 * Представление игры RogueLike.
 *
 * Отвечает за отображение игрового состояния пользователю:
 * карты уровня, характеристик героя, журнала событий,
 * меню, инвентаря и статистики.
 *
 * Класс или интерфейс относится к слою View в архитектуре MVC.
 *
 * @see view.presentation.RoguePresenter
 */

public class RogueView {
    private static final int SCREEN_WIDTH = 82;
    private static final int SCREEN_HEIGHT = 36;
    private final Screen screen;
    private final TextGraphics textGraphics;

    public RogueView(Screen screen) {
        this.screen = screen;
        this.textGraphics = screen.newTextGraphics();
    }

    public KeyStroke readInput() throws IOException {
        KeyStroke stroke;
//        long currentTime = System.currentTimeMillis();
//        do {
        stroke = screen.readInput();
//        } while (stroke == null);
        return stroke;
    }

    public void renderStartMenu(List<String> menuLabels, List<Boolean> menuEnabledStates, int selectedOption) throws IOException {
        screen.clear();

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.putString(35, 5, "ROGUE GAME");
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(30, 8, "Main Menu");

        int startY = 10;

        for (int i = 0; i < menuLabels.size(); i++) {
            String label = menuLabels.get(i);
            boolean enabled = menuEnabledStates.get(i);
            boolean selected = (i == selectedOption);

            int yPosition = startY + i * 2;

            TextColor textColor;
            if (selected) {
                textColor = TextColor.ANSI.YELLOW;
            } else if (!enabled) {
                textColor = TextColor.ANSI.BLACK_BRIGHT;
            } else {
                textColor = TextColor.ANSI.WHITE;
            }
            textGraphics.setForegroundColor(textColor);
            String prefix = (selected) ? "> " : "  ";
            String menuText = prefix + label;
            textGraphics.putString(30, yPosition, menuText);
        }

        int hintsStartY = startY + menuLabels.size() * 2 + 2;

        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        textGraphics.putString(25, hintsStartY, "Use Arrow Keys to navigate");
        textGraphics.putString(28, hintsStartY + 1, "Enter to select");
        textGraphics.putString(32, hintsStartY + 2, "Q to quit");

        screen.refresh();
    }

    public void renderNameInput(String currentName) throws IOException {
        screen.clear();

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.putString(30, 5, "ENTER YOUR NAME");

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(25, 8, "Name: " + currentName + "_");

        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        textGraphics.putString(20, 12, "Type your name and press Enter");
        textGraphics.putString(25, 13, "Backspace to delete");
        textGraphics.putString(30, 14, "Escape to go back");

        screen.refresh();
    }

    public void renderGame(GamePresentation presentation) throws IOException {
        screen.clear();

        renderGameField(presentation.field());
        renderCharacterInfo(presentation.player());

        var ui = presentation.ui();
        if (ui.showSelection()) {
            renderItemSelection(ui);
        } else {
            renderGameLog(presentation.logMessages());
        }
        
        screen.refresh();
    }

    private void renderGameField(FieldPresentation field) {
        for (int y = 0; y < field.height(); y++) {
            for (int x = 0; x < field.width(); x++) {
                char symbol = field.getSymbolAt(y, x);
                TextColor color = field.getColorAt(y, x);

                textGraphics.setForegroundColor(color);
                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                textGraphics.setCharacter(x, y, symbol);
            }
        }
    }

    private void renderCharacterInfo(PlayerPresentation player) throws IOException {
        int infoLine = 20;
        for (int j = 0; j < SCREEN_WIDTH; j++) {
            textGraphics.setCharacter(j, infoLine, ' ');
        }

        StringBuilder info = new StringBuilder();

        info.append(player.levelInfo()).append(" ");
        info.append(player.healthBar()).append(" ");
        info.append(player.stats()).append(" ");
        info.append(player.goldInfo()).append(" ");
        info.append("Keys ");

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.putString(0, infoLine, info.toString());

        renderKeys(player.keys(), info.length(), infoLine);
    }

    private void renderKeys(List<PlayerPresentation.KeyDisplay> keys, int startX, int y) {
        for (int i = 0; i < keys.size(); i++) {
            PlayerPresentation.KeyDisplay key = keys.get(i);
            int xPos = startX + i;

            textGraphics.setForegroundColor(key.color());
            textGraphics.setCharacter(xPos, y, key.symbol());
        }
    }

    private void renderItemSelection(UIPresentation ui) {
        int startY = 22;

        for (int i = startY; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                textGraphics.setCharacter(j, i, ' ');
            }
        }

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(0, startY, ui.prompt());

        var itemOptions = ui.itemOptions();
        for (int i = 0; i < itemOptions.size(); i++) {
            int yPos = startY + 2 + i;
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(2, yPos, itemOptions.get(i));
        }

        if (ui.showUnequipOption()) {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(2, startY + 2 + itemOptions.size(), ui.unequipOptionText());
        }

        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        textGraphics.putString(0, startY + 4 + itemOptions.size(), "Press any button");
    }

    private void renderGameLog(List<String> logMessages) {
        if (logMessages == null) return;
        int startY = 22;
        int logHeight = SCREEN_HEIGHT - startY - 1;

        for (int i = startY; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                textGraphics.setCharacter(j, i, ' ');
            }
        }

        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(0, startY, "Game log. Press B to open backpack");

        for (int i = 0; i < Math.min(logMessages.size(), logHeight - 1); i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(2, startY + 2 + i, logMessages.get(i));
        }
    }

    public void renderGameOver() throws IOException {
        screen.clear();

        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        textGraphics.putString(35, 5, "GAME OVER");

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.putString(30, 10, "You have been defeated!");

        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(25, 15, "Press any key to return to main menu");
        screen.refresh();
    }

    public void renderStatistics(List<String> stats) throws IOException {
        screen.clear();
        TerminalSize size = screen.getTerminalSize();
        int width = size.getColumns();
        int height = size.getRows();

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

        String title = "STATISTICS";
        int titleX = (width - title.length()) / 2;
        int titleY = 1;
        textGraphics.putString(titleX, titleY, title);

        int headerY = 3;
        String header = String.format(
                "%-3s %-10s %-8s %-3s %-7s %-4s %-7s %-7s %-6s %-6s %-5s",
                "#", "Name", "Treasure", "Lvl", "Enemies", "Food", "Elixirs", "Scrolls", "HitD", "HitT", "Cells"
        );
        textGraphics.putString(2, headerY, header);

        int startY = headerY + 2;
        int maxRows = height - startY - 3;
        int rows = Math.min(stats.size(), Math.max(maxRows, 0));

        for (int i = 0; i < rows; i++) {
            int rowY = startY + i;
            textGraphics.putString(2, rowY, stats.get(i));
        }

        String footer = "Esc - back to menu";
        int footerY = height - 2;
        int footerX = (width - footer.length()) / 2;
        textGraphics.putString(footerX, footerY, footer);

        screen.refresh();
    }
}
