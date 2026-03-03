package view.presentation.mapper;

import com.googlecode.lanterna.TextColor;
import controller.ui.UIState;
import datalayer.dto.AttemptStatsDTO;
import model.domain.FogState;
import model.domain.GameState;
import model.domain.items.Item;
import view.presentation.dto.ItemDisplayDTO;
import view.presentation.dto.FieldPresentation;
import view.presentation.dto.GamePresentation;
import view.presentation.dto.PlayerPresentation;
import view.presentation.dto.UIPresentation;

import java.util.ArrayList;
import java.util.List;

public class PresentationMapper {
    public GamePresentation mapToPresentation(GameState gameState, UIState uiState) {
        GamePresentation gamePresentation = new GamePresentation(
                mapField(gameState.field(), gameState.fog()),
                mapPlayer(gameState),
                mapUI(uiState),
                gameState.logMessages() != null ? gameState.logMessages() : List.of()
        );
        return gamePresentation;
    }

    private FieldPresentation mapField(int[][] field, FogState[][] fog) {
        int height = field.length;
        int width = field[0].length;

        char[][] symbols = new char[height][width];
        TextColor[][] colors = new TextColor[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cell = field[i][j];
                FogState state = fog[i][j];
                switch (state) {
                    case HIDDEN:
                        // НЕИССЛЕДОВАННОЕ: АБСОЛЮТНО НИЧЕГО не показываем
                        symbols[i][j] = ' ';
                        colors[i][j] = TextColor.ANSI.BLACK;
                        break;

                    case EXPLORED:
                        // ИССЛЕДОВАННОЕ: показываем только пол, стены, двери
                        // НЕ показываем врагов, предметы, игрока
                        symbols[i][j] = getExploredSymbol(cell);
                        colors[i][j] = getExploredColor(cell);
                        break;

                    case VISIBLE:
                        // ВИДИМОЕ: показываем всё
                        symbols[i][j] = getSymbolForCell(cell);
                        colors[i][j] = getColorForCell(cell);
                        break;
                }
            }
        }

        return new FieldPresentation(symbols, colors, width, height);
    }

    private PlayerPresentation mapPlayer(GameState gameState) {
        String levelInfo = String.format("Level %02d %s",
                gameState.numberOfLevel(), gameState.name());
        String healthBar = formatHealthBar(gameState.curHp(), gameState.maxHp());
        String stats = String.format("Str %03d Dex %03d",
                gameState.str(), gameState.dex());
        String goldInfo = String.format("Gold %05d", gameState.score());
        List<PlayerPresentation.KeyDisplay> keys = mapKeys(gameState.keys());
        return new PlayerPresentation(levelInfo, healthBar, stats, goldInfo, keys);
    }

    private List<PlayerPresentation.KeyDisplay> mapKeys(boolean[] keys) {
        char[] keyLetters = {'B', 'Y', 'R'};
        TextColor[] keyColors = {
                TextColor.ANSI.BLUE_BRIGHT,
                TextColor.ANSI.YELLOW,
                TextColor.ANSI.RED
        };
        TextColor missingColor = TextColor.ANSI.BLACK;
        List<PlayerPresentation.KeyDisplay> keyDisplays = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            char symbol = keyLetters[i];
            TextColor color = keys[i] ? keyColors[i] : missingColor;
            boolean hasKey = keys[i];

            keyDisplays.add(new PlayerPresentation.KeyDisplay(symbol, color, hasKey));
        }

        return keyDisplays;
    }

    private UIPresentation mapUI(UIState uiState) {
        if (!uiState.showItemSelection()) return UIPresentation.normal();

        List<String> itemOptions = mapItemOptions(uiState.availableItems());
        boolean showUnequip = "WEAPON".equals(uiState.selectionType());
        String unequipText = "0 - Unequip current weapon";
        return UIPresentation.withSelection(
                uiState.selectionPrompt(),
                itemOptions,
                showUnequip,
                unequipText
        );
    }

    private char getSymbolForCell(int cell) {
        return switch (cell) {
            case 0 -> ' ';
            case 1 -> '.';
            case 2 -> '_';
            case 3 -> '#';
            case 4, 5, 6 -> 'D';
            case 7, 8, 9 -> 'k';
            case 10 -> 'H';
            case 11 -> 'T';
            case 12 -> 'F';
            case 13 -> 'e';
            case 14 -> 'S';
            case 15 -> 'W';
            case 20 -> 'z';
            case 21 -> 'v';
            case 22 -> 'g';
            case 23 -> 'O';
            case 24 -> 's';
            case 25 -> 'm';
            case 40 -> 'E';
            default -> '!';
        };
    }

    /*
    Напоминание по значению знаков
    0 -> Пустое пространство
    1 -> Пол в комнате
    2 -> Пол в коридоре
    3 -> Стена
    4 - 6 -> Закрытая дверь
    7 - 9 -> Ключ от двери
    10 -> Персонаж
    11 -> Сокровище
    12 -> Еда
    13 -> Эликсир
    14 -> Свиток
    15 -> Оружие
    20 -> Зомби
    21 -> Вампир
    22 -> Призрак
    23 -> Огр
    24 -> Змей-маг
    25 -> Мимик
    40 -> Выход
     */

    private TextColor getColorForCell(int cell) {
        return switch (cell) {
            case 4, 7 -> TextColor.ANSI.BLUE_BRIGHT;
            case 5, 8, 23 -> TextColor.ANSI.YELLOW;
            case 6, 9, 21 -> TextColor.ANSI.RED;
            case 10, 11 -> TextColor.ANSI.MAGENTA;
            case 12 -> TextColor.ANSI.WHITE;
            case 13, 14 -> TextColor.ANSI.GREEN_BRIGHT;
            case 15 -> TextColor.ANSI.RED_BRIGHT;
            case 22, 24, 25 -> TextColor.ANSI.WHITE_BRIGHT;
            case 20, 40 -> TextColor.ANSI.GREEN;
            default -> new TextColor.RGB(255, 165, 0);
        };
    }

    // Новый метод для символов в исследованных областях
    private char getExploredSymbol(int cell) {
        // Показываем только геометрию, НЕ сущности!
        return switch (cell) {
            case 3, 4, 5, 6-> '#';  // Стена
            // ВСЕ остальное - пустота
            case 10, 11, 12, 13, 14, 15,  // Игрок, предметы
                 20, 21, 22, 23, 24, 25, 40 -> ' ';  // Враги, выход
            default -> ' ';
        };
    }

    // Новый метод для цветов в исследованных областях
    private TextColor getExploredColor(int cell) {
        // Темные, приглушенные цвета
        return switch (cell) {
            case 3, 4, 5, 6-> new TextColor.RGB(60, 60, 60);     // Темно-серые стены
            default -> TextColor.ANSI.BLACK;
        };
    }

    private String formatHealthBar(int currentHp, int maxHp) {
        float healthPercentage = (float) currentHp / maxHp;
        int filled = (int) (10 * healthPercentage);
        int empty = 10 - filled;
        StringBuilder bar = new StringBuilder();

        bar.append("HP[");
        for (int i = 0; i < filled; i++) bar.append('█');
        for (int i = 0; i < empty; i++) bar.append('░');
        bar.append(String.format("] %3d/%3d ", currentHp, maxHp));

        return bar.toString();
    }
    private String formatKeys(boolean[] keys) {
        char[] keyLetters = {'B', 'Y', 'R'};
        StringBuilder keysDisplay = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            keysDisplay.append(keyLetters[i]);
        }
        return keysDisplay.toString();
    }

    private List<String> mapItemOptions(List<ItemDisplayDTO> items) {
        List<String> options = new ArrayList<>();
        for (ItemDisplayDTO item : items) {
            options.add((item.index() + 1) + " - " + item.displayName());
        }
        return options;
    }

    public List<ItemDisplayDTO> mapItemsToDisplayDTO(List<Item> items) {
        List<ItemDisplayDTO>  result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            result.add(mapItemToDisplayDTO(item, i));
        }
        return result;
    }

    public ItemDisplayDTO mapItemToDisplayDTO(Item item, int index) {
        String displayName = item.toString();
        String itemType = item.getType().name();
        return new ItemDisplayDTO(displayName, index, itemType);
    }

    public List<String> mapAttemptsToLines(List<AttemptStatsDTO> stats) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < stats.size(); i++) {
            AttemptStatsDTO attempt = stats.get(i);
            String name = attempt.name() == null ? "" : attempt.name();
            if (name.length() > 10) {
                name = name.substring(0, 10);
            }
            String line = String.format(
                    "%-3d %-10s %-8d %-3d %-7d %-4d %-7d %-7d %-6d %-6d %-5d",
                    i + 1,
                    name,
                    attempt.treasuresCollected(),
                    attempt.reachedLevel(),
                    attempt.enemiesDefeated(),
                    attempt.foodEaten(),
                    attempt.elixirsUsed(),
                    attempt.scrollsRead(),
                    attempt.hitsDealt(),
                    attempt.hitsTaken(),
                    attempt.cellsTraveled()
            );
            lines.add(line);
        }
        return lines;
    }
}
